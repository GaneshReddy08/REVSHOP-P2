package com.revshopproject.revshop.dto;

import java.math.BigDecimal;

public class ProductRequestDTO {

    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal mrp;
    private Integer stock;
    private Integer inventoryThreshold;
    private Long sellerId;
    private Long categoryId;

    public ProductRequestDTO() {
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

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
