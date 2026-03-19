package com.revshopproject.revshop.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "CATEGORY")
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cat_gen")
	@SequenceGenerator(name = "cat_gen", sequenceName = "CATEGORY_SEQ", allocationSize = 1)
	private Long categoryId;

	@Column(nullable = false)
	private String name;

	public Category() {
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}