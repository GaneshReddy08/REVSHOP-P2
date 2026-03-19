package com.revshopproject.revshop.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.revshopproject.revshop.entity.Product;
import com.revshopproject.revshop.entity.ProductImage;

public class ProductResponseDTO {

    private Long productId;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal mrp;
    private Integer stock;
    private Integer inventoryThreshold;
    private Long categoryId;
    private String categoryName;
    private String sellerBusinessName;
    private Double averageRating;
    private String primaryImageUrl;
    private List<String> imageUrls;
    private boolean isFavorited;
    private Long sellerId;
 // ADD this field after averageRating:
    private Integer reviewCount;

    public ProductResponseDTO() {
    }

    public static ProductResponseDTO fromEntity(Product product) {
        if (product == null) return null;
        
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setMrp(product.getMrp());
        dto.setStock(product.getStock());
        dto.setInventoryThreshold(product.getInventoryThreshold());
     // ADD this line inside fromEntity(), after dto.setAverageRating(...):
        dto.setReviewCount(product.getReviews() != null ? product.getReviews().size() : 0);
        
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getCategoryId());
            dto.setCategoryName(product.getCategory().getName());
        }
        if (product.getSeller() != null) {
            dto.setSellerId(product.getSeller().getUserId());
            dto.setSellerBusinessName(product.getSeller().getBusinessName());
        }
        
        dto.setAverageRating(product.getAverageRating());
        dto.setPrimaryImageUrl(product.getPrimaryImageUrl());
        
        if (product.getImages() != null) {
            dto.setImageUrls(product.getImages().stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }

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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSellerBusinessName() {
        return sellerBusinessName;
    }

    public void setSellerBusinessName(String sellerBusinessName) {
        this.sellerBusinessName = sellerBusinessName;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public String getPrimaryImageUrl() {
        return primaryImageUrl;
    }

    public void setPrimaryImageUrl(String primaryImageUrl) {
        this.primaryImageUrl = primaryImageUrl;
    }

    public boolean isFavorited() {
        return isFavorited;
    }

    public void setFavorited(boolean favorited) {
        isFavorited = favorited;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

	public Integer getReviewCount() {
		return reviewCount;
	}

	public void setReviewCount(Integer reviewCount) {
		this.reviewCount = reviewCount;
	}

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }
    
    
}
