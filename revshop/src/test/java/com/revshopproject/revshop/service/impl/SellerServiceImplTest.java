package com.revshopproject.revshop.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.revshopproject.revshop.entity.OrderItem;
import com.revshopproject.revshop.entity.Product;
import com.revshopproject.revshop.repository.OrderItemRepository;
import com.revshopproject.revshop.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
public class SellerServiceImplTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private SellerServiceImpl sellerService;

    @Test
    void testGetSellerStats() {
        Long sellerId = 10L;
        List<OrderItem> items = new ArrayList<>();
        
        OrderItem item1 = new OrderItem();
        item1.setPrice(new BigDecimal("50.00"));
        item1.setQuantity(2); // 100.00
        
        OrderItem item2 = new OrderItem();
        item2.setPrice(new BigDecimal("25.00"));
        item2.setQuantity(1); // 25.00
        
        items.add(item1);
        items.add(item2);

        List<Product> products = new ArrayList<>();
        products.add(new Product());
        products.add(new Product());
        products.add(new Product()); // 3 products

        when(orderItemRepository.findByProduct_Seller_UserIdOrderByOrder_OrderIdDesc(sellerId)).thenReturn(items);
        when(productRepository.findBySeller_UserId(sellerId)).thenReturn(products);

        Map<String, Object> stats = sellerService.getSellerStats(sellerId);
        
        assertEquals(new BigDecimal("125.00"), stats.get("totalRevenue"));
        assertEquals(2, stats.get("totalItemsSold"));
        assertEquals(3, stats.get("activeProducts"));
    }

    @Test
    void testGetSellerOrders() {
        Long sellerId = 10L;
        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem());
        items.add(new OrderItem());

        when(orderItemRepository.findByProduct_Seller_UserIdOrderByOrder_OrderIdDesc(sellerId)).thenReturn(items);

        List<OrderItem> result = sellerService.getSellerOrders(sellerId);
        assertEquals(2, result.size());
    }
}
