package com.revshopproject.revshop.dto;

public class OrderRequestDTO {
    private Long userId;
    private String shippingAddress;
    private String paymentMethod;

    public OrderRequestDTO() {
    }

    public OrderRequestDTO(Long userId, String shippingAddress, String paymentMethod) {
        this.userId = userId;
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
