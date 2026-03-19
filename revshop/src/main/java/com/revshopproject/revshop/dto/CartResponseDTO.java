package com.revshopproject.revshop.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.revshopproject.revshop.entity.Cart;

public class CartResponseDTO {

    private Long cartId;
    private List<CartItemResponseDTO> items;
    private BigDecimal totalAmount;

    public CartResponseDTO() {
    }

    public static CartResponseDTO fromEntity(Cart cart) {
        if (cart == null) return null;
        
        CartResponseDTO dto = new CartResponseDTO();
        dto.setCartId(cart.getCartId());
        
        if (cart.getItems() != null) {
            List<CartItemResponseDTO> itemDTOs = cart.getItems().stream()
                    .map(CartItemResponseDTO::fromEntity)
                    .collect(Collectors.toList());
            dto.setItems(itemDTOs);
            
            BigDecimal total = itemDTOs.stream()
                    .map(item -> item.getSubTotal() != null ? item.getSubTotal() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            dto.setTotalAmount(total);
        } else {
            dto.setTotalAmount(BigDecimal.ZERO);
        }
        
        return dto;
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public List<CartItemResponseDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemResponseDTO> items) {
        this.items = items;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
