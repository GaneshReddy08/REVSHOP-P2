package com.revshopproject.revshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revshopproject.revshop.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}