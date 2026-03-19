package com.revshopproject.revshop.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "CART_ITEMS", uniqueConstraints = {@UniqueConstraint(columnNames = {"CART_ID", "PRODUCT_ID"})})
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cartitem_gen")
    @SequenceGenerator(name = "cartitem_gen", sequenceName = "CARTITEM_SEQ", allocationSize = 1)
    private Long cartItemId;

    @ManyToOne
    @JoinColumn(name = "CART_ID", nullable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    private Integer quantity;

    public CartItem() {}
    public CartItem(Long cartItemId, Cart cart, Product product, Integer quantity) {
        this.cartItemId = cartItemId;
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
    }

    public Long getCartItemId() { return cartItemId; }
    public void setCartItemId(Long cartItemId) { this.cartItemId = cartItemId; }

    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}