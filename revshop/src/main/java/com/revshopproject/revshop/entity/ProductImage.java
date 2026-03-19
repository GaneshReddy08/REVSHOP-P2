package com.revshopproject.revshop.entity;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "PRODUCT_IMAGES")
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "img_gen")
    @SequenceGenerator(name = "img_gen", sequenceName = "PRODUCT_IMAGE_SEQ", allocationSize = 1)
    private Long imageId;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    @JsonBackReference
    private Product product;

    @Column(name = "IMAGE_URL", nullable = false)
    private String imageUrl;

    public ProductImage() {}

    public Long getImageId() { return imageId; }
    public void setImageId(Long imageId) { this.imageId = imageId; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}