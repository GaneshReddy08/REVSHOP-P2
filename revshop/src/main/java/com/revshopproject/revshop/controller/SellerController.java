package com.revshopproject.revshop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revshopproject.revshop.entity.OrderItem;
import com.revshopproject.revshop.service.ProductService;
import com.revshopproject.revshop.service.SellerService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/seller")
public class SellerController {

    private final SellerService sellerService;
    private final ProductService productService;

    public SellerController(SellerService sellerService, ProductService productService) {
        this.sellerService = sellerService;
        this.productService = productService;
    }

    // GET: http://localhost:8888/api/seller/10/stats
    @GetMapping("/{sellerId}/stats")
    public ResponseEntity<Map<String, Object>> getStats(@PathVariable Long sellerId) {
        return ResponseEntity.ok(sellerService.getSellerStats(sellerId));
    }

    // GET: http://localhost:8888/api/seller/10/inventory
    @GetMapping("/{sellerId}/inventory")
    public ResponseEntity<List<com.revshopproject.revshop.dto.ProductResponseDTO>> getInventory(@PathVariable Long sellerId) {
        List<com.revshopproject.revshop.dto.ProductResponseDTO> dtos = productService.getProductsBySellerId(sellerId)
                .stream()
                .map(com.revshopproject.revshop.dto.ProductResponseDTO::fromEntity)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // GET: http://localhost:8888/api/seller/10/orders
    @GetMapping("/{sellerId}/orders")
    public ResponseEntity<List<OrderItem>> getOrders(@PathVariable Long sellerId) {
        return ResponseEntity.ok(sellerService.getSellerOrders(sellerId));
    }
}