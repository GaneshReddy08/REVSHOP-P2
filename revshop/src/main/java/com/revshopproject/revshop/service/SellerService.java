package com.revshopproject.revshop.service;

// import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.revshopproject.revshop.entity.OrderItem;

public interface SellerService {
	Map<String, Object> getSellerStats(Long sellerId);

	List<OrderItem> getSellerOrders(Long sellerId);
	
}