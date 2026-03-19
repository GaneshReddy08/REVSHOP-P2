package com.revshopproject.revshop.service;

import java.util.List;

import com.revshopproject.revshop.dto.OrderRequestDTO;
import com.revshopproject.revshop.dto.OrderResponseDTO;

public interface OrderService {
	OrderResponseDTO placeOrder(OrderRequestDTO dto);
    OrderResponseDTO acceptOrder(Long orderId);
    OrderResponseDTO confirmPaymentAndAccept(Long orderId);
    OrderResponseDTO shipOrder(Long orderId);
    OrderResponseDTO markAsDelivered(Long orderId);
    OrderResponseDTO updateOrderStatus(Long orderId, String newStatus);
    List<OrderResponseDTO> getOrdersByUserId();
    OrderResponseDTO getOrderById(Long orderId);}