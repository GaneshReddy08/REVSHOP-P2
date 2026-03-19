package com.revshopproject.revshop.service.impl;

import java.math.BigDecimal;
//ADD this import at the top of OrderServiceImpl.java
import com.revshopproject.revshop.entity.Payment;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revshopproject.revshop.dto.OrderRequestDTO;
import com.revshopproject.revshop.dto.OrderResponseDTO;
import com.revshopproject.revshop.entity.*;
import com.revshopproject.revshop.repository.*;
import com.revshopproject.revshop.service.NotificationService;
import com.revshopproject.revshop.service.OrderService;
import com.revshopproject.revshop.service.UserService;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LogManager.getLogger(OrderServiceImpl.class);

	private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final NotificationService notificationService;
    private final UserService userService;
    private final PaymentRepository paymentRepository;

    public OrderServiceImpl(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
            CartItemRepository cartItemRepository, CartRepository cartRepository,
            ProductRepository productRepository, NotificationService notificationService,
            UserService userService, PaymentRepository paymentRepository) {
        this.orderRepository     = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository  = cartItemRepository;
        this.cartRepository      = cartRepository;
        this.productRepository   = productRepository;
        this.notificationService = notificationService;
        this.userService         = userService;
        this.paymentRepository   = paymentRepository;
    }

    @Override
    @Transactional
    public OrderResponseDTO placeOrder(OrderRequestDTO dto) {
        if (dto.getPaymentMethod() == null)
            throw new RuntimeException("Payment method cannot be empty!");

        String normalizedPayment = dto.getPaymentMethod().trim().toUpperCase();
        if (!normalizedPayment.equals("CREDIT_CARD") && !normalizedPayment.equals("DEBIT_CARD")
                && !normalizedPayment.equals("UPI") && !normalizedPayment.equals("COD")
                && !normalizedPayment.equals("NET_BANKING"))
            throw new RuntimeException("Invalid payment method.");

        User currentUser = userService.getCurrentUser();
        Cart cart = cartRepository.findByUser_UserId(currentUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        List<CartItem> cartItems = cartItemRepository.findByCart_CartId(cart.getCartId());
        if (cartItems.isEmpty()) throw new RuntimeException("Cart is empty!");

        Order order = new Order();
        order.setUser(cart.getUser());
        order.setShippingAddress(dto.getShippingAddress());
        order.setPaymentMethod(normalizedPayment);
        order.setStatus("PENDING");
        order = orderRepository.save(order);

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> savedItems = new ArrayList<>();

        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            if (product.getStock() < item.getQuantity())
                throw new RuntimeException("Product " + product.getName() + " is out of stock!");

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(product.getPrice());
            savedItems.add(orderItemRepository.save(orderItem));
            total = total.add(product.getPrice().multiply(new BigDecimal(item.getQuantity())));

            notificationService.sendNotification(product.getSeller(),
                    "[PLACED] New Order: " + product.getName() + " (Qty: " + item.getQuantity()
                    + ") — Payment: " + normalizedPayment);
        }

        order.setTotalAmount(total);
        cartItemRepository.deleteAll(cartItems);
        order = orderRepository.save(order);
        log.info("Order placed: orderId={}, userId={}, total={}, payment={}",
                order.getOrderId(), currentUser.getUserId(), total, normalizedPayment);

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(normalizedPayment);
        payment.setAmount(total);
        // COD is pending until delivery; others need seller confirmation
        payment.setPaymentStatus(normalizedPayment.equals("COD") ? "PENDING" : "AWAITING_CONFIRMATION");
        paymentRepository.save(payment);

        notificationService.sendNotification(order.getUser(),
                "[PLACED] Order #" + order.getOrderId() + " placed! "
                + (normalizedPayment.equals("COD")
                        ? "Pay ₹" + total + " on delivery."
                        : "Awaiting seller payment confirmation for " + normalizedPayment + "."));

        return enrichWithPayment(order, savedItems);
    }

    @Override
    @Transactional
    public OrderResponseDTO acceptOrder(Long orderId) {
        User currentSeller = userService.getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!"PENDING".equals(order.getStatus()))
            throw new RuntimeException("Cannot accept. Current status: " + order.getStatus());

        if (!"COD".equals(order.getPaymentMethod()))
            throw new RuntimeException("This order uses " + order.getPaymentMethod()
                    + ". Please use 'Confirm Payment Received' to accept it.");

        List<OrderItem> items = ownershipCheck(orderId, currentSeller);
        deductInventory(items);

        order.setStatus("ACCEPTED");
        notificationService.sendNotification(order.getUser(),
                "[ACCEPTED] Your COD order #" + order.getOrderId()
                + " accepted. Pay ₹" + order.getTotalAmount() + " on delivery.");
        order = orderRepository.save(order);
        log.info("Order accepted (COD): orderId={}, sellerId={}", orderId, currentSeller.getUserId());
        return enrichWithPayment(order, items);
    }

    @Override
    @Transactional
    public OrderResponseDTO confirmPaymentAndAccept(Long orderId) {
        User currentSeller = userService.getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!"PENDING".equals(order.getStatus()))
            throw new RuntimeException("Cannot confirm payment. Current status: " + order.getStatus());

        if ("COD".equals(order.getPaymentMethod()))
            throw new RuntimeException("COD orders do not need payment confirmation. Use Accept instead.");

        List<OrderItem> items = ownershipCheck(orderId, currentSeller);

        Payment payment = paymentRepository.findByOrder_OrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment record not found for order #" + orderId));
        payment.setPaymentStatus("COMPLETED");
        paymentRepository.save(payment);

        deductInventory(items);

        order.setStatus("ACCEPTED");
        notificationService.sendNotification(order.getUser(),
                "[PAYMENT CONFIRMED] Seller confirmed your " + order.getPaymentMethod()
                + " payment for order #" + order.getOrderId() + ". Order accepted!");
        order = orderRepository.save(order);
        log.info("Payment confirmed and order accepted: orderId={}, sellerId={}", orderId, currentSeller.getUserId());
        return enrichWithPayment(order, items);
    }

    @Override
    @Transactional
    public OrderResponseDTO shipOrder(Long orderId) {
        User currentSeller = userService.getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!"ACCEPTED".equals(order.getStatus()))
            throw new RuntimeException("Cannot ship. Order must be ACCEPTED. Current: " + order.getStatus());

        List<OrderItem> items = ownershipCheck(orderId, currentSeller);
        order.setStatus("SHIPPED");
        notificationService.sendNotification(order.getUser(),
                "[SHIPPED] 🚚 Your order #" + order.getOrderId() + " is on its way!");
        order = orderRepository.save(order);
        log.info("Order shipped: orderId={}, sellerId={}", orderId, currentSeller.getUserId());
        return enrichWithPayment(order, items);
    }

    @Override
    @Transactional
    public OrderResponseDTO markAsDelivered(Long orderId) {
        User currentSeller = userService.getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!"SHIPPED".equals(order.getStatus()) && !"ACCEPTED".equals(order.getStatus()))
            throw new RuntimeException("Cannot deliver. Order must be SHIPPED or ACCEPTED. Current: " + order.getStatus());

        List<OrderItem> items = ownershipCheck(orderId, currentSeller);

        // COD: cash collected at door → mark payment COMPLETED
        if ("COD".equals(order.getPaymentMethod())) {
            Payment payment = paymentRepository.findByOrder_OrderId(orderId)
                    .orElseThrow(() -> new RuntimeException("Payment record not found for order #" + orderId));
            payment.setPaymentStatus("COMPLETED");
            paymentRepository.save(payment);
        }

        order.setStatus("DELIVERED");
        String note = "COD".equals(order.getPaymentMethod())
                ? " Cash payment of ₹" + order.getTotalAmount() + " collected." : "";
        notificationService.sendNotification(order.getUser(),
                "[DELIVERED] 📦 Order #" + orderId + " delivered!" + note);
        order = orderRepository.save(order);
        log.info("Order delivered: orderId={}, sellerId={}", orderId, currentSeller.getUserId());
        return enrichWithPayment(order, items);
    }

    @Override
    @Transactional
    public OrderResponseDTO updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        User currentUser = userService.getCurrentUser();
        String status = newStatus.toUpperCase();

        boolean isBuyer = order.getUser().getUserId().equals(currentUser.getUserId());
        List<OrderItem> items = orderItemRepository.findByOrder_OrderId(orderId);
        boolean isSeller = items.stream()
                .anyMatch(i -> i.getProduct().getSeller().getUserId().equals(currentUser.getUserId()));

        if (!isBuyer && !isSeller)
            throw new RuntimeException("Unauthorized: You do not have access to this order.");

        if ("CANCELLED".equals(status) && isBuyer) {
            if (!"PENDING".equals(order.getStatus()) && !"ACCEPTED".equals(order.getStatus()))
                throw new RuntimeException("Cannot cancel order in current state: " + order.getStatus());
        }

        if (("CANCELLED".equals(status) || "REJECTED".equals(status)) && "ACCEPTED".equals(order.getStatus()))
            reverseInventory(orderId);

        if ("CANCELLED".equals(status) || "REJECTED".equals(status)) {
            paymentRepository.findByOrder_OrderId(orderId).ifPresent(p -> {
                if (!"COMPLETED".equals(p.getPaymentStatus())) {
                    p.setPaymentStatus("FAILED");
                    paymentRepository.save(p);
                }
            });
        }

        order.setStatus(status);

        if ("REJECTED".equals(status)) {
            notificationService.sendNotification(order.getUser(),
                    "[REJECTED] Order #" + orderId + " was rejected by the seller.");
        } else if ("CANCELLED".equals(status)) {
            items.forEach(i -> notificationService.sendNotification(i.getProduct().getSeller(),
                    "[CANCELLED] Order #" + orderId + " was cancelled by the customer."));
            notificationService.sendNotification(order.getUser(),
                    "[CANCELLED] You have cancelled order #" + orderId + ".");
        } else {
            notificationService.sendNotification(order.getUser(),
                    "[" + status + "] Order #" + orderId + " status updated to: " + status);
        }

        order = orderRepository.save(order);
        log.info("Order status updated: orderId={}, newStatus={}, updatedBy userId={}",
                orderId, status, currentUser.getUserId());
        return enrichWithPayment(order, orderItemRepository.findByOrder_OrderId(orderId));
    }

    @Override
    public List<OrderResponseDTO> getOrdersByUserId() {
        User currentUser = userService.getCurrentUser();
        return orderRepository.findByUser_UserIdOrderByOrderIdDesc(currentUser.getUserId()).stream()
                .map(order -> enrichWithPayment(order, orderItemRepository.findByOrder_OrderId(order.getOrderId())))
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDTO getOrderById(Long orderId) {
        User currentUser = userService.getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        boolean isBuyer = order.getUser().getUserId().equals(currentUser.getUserId());
        List<OrderItem> items = orderItemRepository.findByOrder_OrderId(orderId);
        boolean isSeller = items.stream()
                .anyMatch(i -> i.getProduct().getSeller().getUserId().equals(currentUser.getUserId()));

        if (!isBuyer && !isSeller)
            throw new RuntimeException("Unauthorized: You do not have access to this order.");

        return enrichWithPayment(order, items);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private OrderResponseDTO enrichWithPayment(Order order, List<OrderItem> items) {
        OrderResponseDTO dto = OrderResponseDTO.fromEntity(order, items);
        paymentRepository.findByOrder_OrderId(order.getOrderId())
                .ifPresent(p -> dto.setPaymentStatus(p.getPaymentStatus()));
        return dto;
    }

    private List<OrderItem> ownershipCheck(Long orderId, User seller) {
        List<OrderItem> items = orderItemRepository.findByOrder_OrderId(orderId);
        boolean owns = items.stream()
                .anyMatch(i -> i.getProduct().getSeller().getUserId().equals(seller.getUserId()));
        if (!owns) throw new RuntimeException("Unauthorized: You do not have products in this order.");
        return items;
    }

    private void deductInventory(List<OrderItem> items) {
        for (OrderItem item : items) {
            Product product = item.getProduct();
            product.setStock(product.getStock() - item.getQuantity());
            int threshold = (product.getInventoryThreshold() != null) ? product.getInventoryThreshold() : 5;
            if (product.getStock() <= threshold)
                notificationService.sendNotification(product.getSeller(),
                        "[LOW STOCK] " + product.getName() + " has only " + product.getStock() + " left.");
            productRepository.save(product);
        }
    }

    private void reverseInventory(Long orderId) {
        List<OrderItem> items = orderItemRepository.findByOrder_OrderId(orderId);
        for (OrderItem item : items) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }
    }
}