package com.revshopproject.revshop.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "favorites")
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "favorite_gen")
    @SequenceGenerator(
        name = "favorite_gen", 
        sequenceName = "FAVORITE_SEQ", 
        allocationSize = 1 // Matches your SQL 'INCREMENT BY 1'
    )
    @Column(name = "FAVORITE_ID")
    private Long favoriteId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Use column definition to match Oracle's TIMESTAMP exactly
    @Column(name = "added_at", insertable = false, updatable = false)
    private LocalDateTime addedAt;

	public Long getFavoriteId() {
		return favoriteId;
	}

	public void setFavoriteId(Long favoriteId) {
		this.favoriteId = favoriteId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public LocalDateTime getAddedAt() {
		return addedAt;
	}

	public void setAddedAt(LocalDateTime addedAt) {
		this.addedAt = addedAt;
	}

    // Remove @PrePersist since the DB handles the default SYSTIMESTAMP now
}