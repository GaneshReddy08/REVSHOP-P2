package com.revshopproject.revshop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revshopproject.revshop.entity.Category;
import com.revshopproject.revshop.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // GET: http://localhost:8888/api/categories
    @GetMapping
    public List<Category> getAll() {
        return categoryService.getAllCategories();
    }

    // POST: http://localhost:8888/api/categories
    @PostMapping
    public ResponseEntity<Category> create(@RequestBody com.revshopproject.revshop.dto.CategoryRequestDTO categoryRequest) {
        return ResponseEntity.ok(categoryService.addCategory(categoryRequest));
    }
}