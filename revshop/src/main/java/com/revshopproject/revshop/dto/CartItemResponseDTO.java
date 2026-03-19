package com.revshopproject.revshop.dto;

import java.math.BigDecimal;

import com.revshopproject.revshop.entity.CartItem;
import com.revshopproject.revshop.entity.Product;

public class CartItemResponseDTO {

    private Long cartItemId;
    private Long productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subTotal;

    public CartItemResponseDTO() {
    }

    public static CartItemResponseDTO fromEntity(CartItem cartItem) {
        if (cartItem == null) return null;
        
        CartItemResponseDTO dto = new CartItemResponseDTO();
        dto.setCartItemId(cartItem.getCartItemId());
        dto.setQuantity(cartItem.getQuantity());
        
        Product product = cartItem.getProduct();
        if (product != null) {
            dto.setProductId(product.getProductId());
            dto.setProductName(product.getName());
            dto.setPrice(product.getPrice());
            if (product.getPrice() != null && cartItem.getQuantity() != null) {
                dto.setSubTotal(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            }
        }
        
        return dto;
    }

    public Long getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(Long cartItemId) {
        this.cartItemId = cartItemId;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }
}
