package com.revshopproject.revshop.service;

import java.util.List;

import com.revshopproject.revshop.dto.CategoryRequestDTO;
import com.revshopproject.revshop.entity.Category;

public interface CategoryService {
    List<Category> getAllCategories();
    Category addCategory(CategoryRequestDTO dto);
    Category getCategoryById(Long id);
}