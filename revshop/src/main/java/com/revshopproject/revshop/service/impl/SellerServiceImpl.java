package com.revshopproject.revshop.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.revshopproject.revshop.entity.OrderItem;
import com.revshopproject.revshop.repository.OrderItemRepository;
import com.revshopproject.revshop.repository.ProductRepository;
import com.revshopproject.revshop.service.SellerService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SellerServiceImpl implements SellerService {

    private static final Logger log = LogManager.getLogger(SellerServiceImpl.class);

    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public SellerServiceImpl(OrderItemRepository orderItemRepository, ProductRepository productRepository) {
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Map<String, Object> getSellerStats(Long sellerId) {
        List<OrderItem> items = orderItemRepository.findByProduct_Seller_UserIdOrderByOrder_OrderIdDesc(sellerId);

        BigDecimal totalRevenue = items.stream()
                .filter(item -> item.getPrice() != null && item.getQuantity() != null)
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int activeProducts = productRepository.findBySeller_UserId(sellerId).size();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRevenue", totalRevenue);
        stats.put("totalItemsSold", items.size());
        stats.put("activeProducts", activeProducts);

        log.info("Seller stats fetched: sellerId={}, totalRevenue={}, itemsSold={}, activeProducts={}",
                sellerId, totalRevenue, items.size(), activeProducts);
        return stats;
    }

    @Override
    public List<OrderItem> getSellerOrders(Long sellerId) {
        List<OrderItem> orders = orderItemRepository.findByProduct_Seller_UserIdOrderByOrder_OrderIdDesc(sellerId);
        log.debug("Fetched {} order items for sellerId={}", orders.size(), sellerId);
        return orders;
    }
}