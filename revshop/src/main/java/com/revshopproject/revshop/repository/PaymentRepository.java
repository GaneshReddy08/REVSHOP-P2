package com.revshopproject.revshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revshopproject.revshop.entity.Payment;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    // Find payment details for a specific order
    Optional<Payment> findByOrder_OrderId(Long orderId);
    
    // You could also add a method to find all payments with a specific status
    // List<Payment> findByPaymentStatus(String status);
}