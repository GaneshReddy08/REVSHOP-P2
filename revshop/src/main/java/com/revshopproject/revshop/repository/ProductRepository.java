package com.revshopproject.revshop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.revshopproject.revshop.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	// Find all products listed by a specific seller
	List<Product> findBySeller_UserId(Long sellerId);

	// Find products by category
	List<Product> findByCategory_CategoryId(Long categoryId);
	// find by name
	List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
	
	@Query("SELECT p FROM Product p LEFT JOIN p.reviews r " +
	           "GROUP BY p.productId, p.name, p.description, p.price, p.stock, p.category, p.seller " + 
	           "ORDER BY AVG(r.rating) DESC NULLS LAST")
	    List<Product> findTopRatedProducts();
}