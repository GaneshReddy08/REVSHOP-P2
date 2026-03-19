package com.revshopproject.revshop.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "PRODUCTS")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prod_gen")
	@SequenceGenerator(name = "prod_gen", sequenceName = "PRODUCT_SEQ", allocationSize = 1)
	private Long productId;

	@Column(nullable = false)
	private String name;

	private String description;

	@Column(nullable = false)
	private BigDecimal price;

	@Column(nullable = false)
	private BigDecimal mrp;

	private Integer stock;

	@Column(name = "INVENTORY_THRESHOLD")
	private Integer inventoryThreshold;

	@ManyToOne
	@JoinColumn(name = "SELLER_ID", nullable = false)
	private User seller;

	@ManyToOne
	@JoinColumn(name = "CATEGORY_ID")
	private Category category;
	
	// Inside Product.java
	@Transient
	private Double averageRating;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<Review> reviews;

	
	// Inside Product.java

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JsonManagedReference
	private List<ProductImage> images;

	@Transient
	public String getPrimaryImageUrl() {
	    if (images != null && !images.isEmpty()) {
	        return images.get(0).getImageUrl();
	    }
	    return "default-placeholder.png"; // Fallback if no image exists
	}
	
	public Double getAverageRating() {
	    if (reviews == null || reviews.isEmpty()) {
	        return 0.0;
	    }
	    double sum = 0;
	    for (Review review : reviews) {
	        sum += review.getRating();
	    }
	    return Math.round((sum / reviews.size()) * 10.0) / 10.0; // Rounds to 1 decimal place
	}

	public Product() {
	}

	// Getters and Setters
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getMrp() {
		return mrp;
	}

	public void setMrp(BigDecimal mrp) {
		this.mrp = mrp;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public Integer getInventoryThreshold() {
		return inventoryThreshold;
	}

	public void setInventoryThreshold(Integer inventoryThreshold) {
		this.inventoryThreshold = inventoryThreshold;
	}

	public User getSeller() {
		return seller;
	}

	public void setSeller(User seller) {
		this.seller = seller;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public List<ProductImage> getImages() {
		return images;
	}

	public void setImages(List<ProductImage> images) {
		this.images = images;
	}

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}

	public void setAverageRating(Double averageRating) {
		this.averageRating = averageRating;
	}

	
}