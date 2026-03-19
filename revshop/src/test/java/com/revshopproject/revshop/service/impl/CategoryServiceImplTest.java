package com.revshopproject.revshop.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.revshopproject.revshop.dto.CategoryRequestDTO;
import com.revshopproject.revshop.entity.Category;
import com.revshopproject.revshop.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setCategoryId(1L);
        testCategory.setName("Electronics");
    }

    @Test
    void testGetAllCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(testCategory);

        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = categoryService.getAllCategories();
        assertEquals(1, result.size());
        assertEquals("Electronics", result.get(0).getName());
    }

    @Test
    void testAddCategory() {
        CategoryRequestDTO dto = new CategoryRequestDTO();
        dto.setName("New Category");

        Category savedCategory = new Category();
        savedCategory.setCategoryId(2L);
        savedCategory.setName("New Category");

        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        Category result = categoryService.addCategory(dto);
        assertEquals(2L, result.getCategoryId());
        assertEquals("New Category", result.getName());
    }

    @Test
    void testGetCategoryById_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        Category result = categoryService.getCategoryById(1L);
        assertEquals(1L, result.getCategoryId());
        assertEquals("Electronics", result.getName());
    }

    @Test
    void testGetCategoryById_NotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> categoryService.getCategoryById(99L));
        assertEquals("Category not found with id: 99", exception.getMessage());
    }
}
