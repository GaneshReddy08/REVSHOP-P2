package com.revshopproject.revshop.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revshopproject.revshop.dto.OrderRequestDTO;
import com.revshopproject.revshop.dto.OrderResponseDTO;
import com.revshopproject.revshop.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 1. PLACE ORDER (Buyer)
    @PostMapping("/place")
    public ResponseEntity<OrderResponseDTO> placeOrder(@RequestBody OrderRequestDTO dto) {
        OrderResponseDTO response = orderService.placeOrder(dto);
        return ResponseEntity.ok(response);
    }

    // 2. GET USER ORDERS (Buyer)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponseDTO>> getUserOrders(@PathVariable Long userId) {
        // userId ignored in favor of authenticated context for security
        return ResponseEntity.ok(orderService.getOrdersByUserId());
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponseDTO>> getMyOrders() {
        return ResponseEntity.ok(orderService.getOrdersByUserId());
    }

    // 3. GET SPECIFIC ORDER
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    // 4. SELLER ACCEPT ORDER
    // PATCH: http://localhost:8888/api/orders/accept/1?sellerId=10
    @PatchMapping("/accept/{orderId}")
    public ResponseEntity<OrderResponseDTO> acceptOrder(@PathVariable Long orderId, @RequestParam Long sellerId) {
        // sellerId ignored in favor of authenticated context for security
        return ResponseEntity.ok(orderService.acceptOrder(orderId));
    }
    
 // Non-COD orders: seller confirms payment received → auto accepts
    @PatchMapping("/confirm-payment/{orderId}")
    public ResponseEntity<OrderResponseDTO> confirmPayment(@PathVariable Long orderId, @RequestParam Long sellerId) {
        return ResponseEntity.ok(orderService.confirmPaymentAndAccept(orderId));
    }

    // Move order from ACCEPTED → SHIPPED
    @PatchMapping("/ship/{orderId}")
    public ResponseEntity<OrderResponseDTO> shipOrder(@PathVariable Long orderId, @RequestParam Long sellerId) {
        return ResponseEntity.ok(orderService.shipOrder(orderId));
    }

//    // Mark as delivered (COD: also collects payment)
//    @PatchMapping("/deliver/{orderId}")
//    public ResponseEntity<OrderResponseDTO> deliverOrder(@PathVariable Long orderId, @RequestParam Long sellerId) {
//        return ResponseEntity.ok(orderService.markAsDelivered(orderId));
//    }

    // 5. SELLER REJECT ORDER
    // PATCH: http://localhost:8888/api/orders/reject/1?sellerId=10
    @PatchMapping("/reject/{orderId}")
    public ResponseEntity<OrderResponseDTO> rejectOrder(@PathVariable Long orderId, @RequestParam Long sellerId) {
        // Rejection logic should also verify ownership
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, "REJECTED"));
    }

    // 6. UPDATE STATUS (General/Admin)
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDTO> updateStatus(@PathVariable Long orderId, @RequestParam String newStatus) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, newStatus));
    }

    // 7. CANCEL ORDER (Buyer/Seller)
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponseDTO> cancelOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, "CANCELLED"));
    }
 // SELLER: Marks the order as delivered
 // PATCH: http://localhost:8888/api/orders/deliver/1?sellerId=10
 @PatchMapping("/deliver/{orderId}")
 public ResponseEntity<OrderResponseDTO> deliverOrder(@PathVariable Long orderId, @RequestParam Long sellerId) {
     return ResponseEntity.ok(orderService.markAsDelivered(orderId));
 }
    
}