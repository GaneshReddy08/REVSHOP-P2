package com.revshopproject.revshop.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.revshopproject.revshop.dto.CategoryRequestDTO;
import com.revshopproject.revshop.entity.Category;
import com.revshopproject.revshop.repository.CategoryRepository;
import com.revshopproject.revshop.service.CategoryService;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger log = LogManager.getLogger(CategoryServiceImpl.class);

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        log.debug("Fetched {} categories", categories.size());
        return categories;
    }

    @Override
    public Category addCategory(CategoryRequestDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        Category saved = categoryRepository.save(category);
        log.info("Category added: '{}' (ID: {})", saved.getName(), saved.getCategoryId());
        return saved;
    }

    @Override
    public Category getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Category not found with id: {}", id);
                    return new RuntimeException("Category not found with id: " + id);
                });
        log.debug("Fetched category: '{}' (ID: {})", category.getName(), id);
        return category;
    }
}