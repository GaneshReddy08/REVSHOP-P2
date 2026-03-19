package com.revshopproject.revshop.dto;

import java.math.BigDecimal;

import com.revshopproject.revshop.entity.OrderItem;

public class OrderItemResponseDTO {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal priceAtPurchase;
    private BigDecimal subTotal;

    public OrderItemResponseDTO() {
    }

    public static OrderItemResponseDTO fromEntity(OrderItem item) {
        OrderItemResponseDTO dto = new OrderItemResponseDTO();
        if (item.getProduct() != null) {
            dto.setProductId(item.getProduct().getProductId());
            dto.setProductName(item.getProduct().getName());
        }
        dto.setQuantity(item.getQuantity());
        // Handling the price based on standard logic
        dto.setPriceAtPurchase(item.getPrice());
        
        if (item.getPrice() != null && item.getQuantity() != null) {
            dto.setSubTotal(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
        } else {
            dto.setSubTotal(BigDecimal.ZERO);
        }
        
        return dto;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPriceAtPurchase() {
        return priceAtPurchase;
    }

    public void setPriceAtPurchase(BigDecimal priceAtPurchase) {
        this.priceAtPurchase = priceAtPurchase;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }
}
