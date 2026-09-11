package com.ecommerce.ecommercebackend.service;

import com.ecommerce.ecommercebackend.dto.CategoryResponseDTO;
import com.ecommerce.ecommercebackend.entity.Category;
import com.ecommerce.ecommercebackend.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;


    // Test 1: Get All Categories
    @Test
    void getAllCategories_shouldReturnCategories() {

        Category category1 = new Category();

        category1.setId(1L);
        category1.setName("Electronics");


        Category category2 = new Category();

        category2.setId(2L);
        category2.setName("Clothing");


        when(categoryRepository.findAll())
                .thenReturn(List.of(category1, category2));


        List<CategoryResponseDTO> result =
                categoryService.getAllCategories();


        assertEquals(2, result.size());

        assertEquals(1L, result.get(0).getId());
        assertEquals("Electronics", result.get(0).getName());

        assertEquals(2L, result.get(1).getId());
        assertEquals("Clothing", result.get(1).getName());


        verify(categoryRepository).findAll();
    }


    // Test 2: Get Category By Id - Success
    @Test
    void getCategoryById_shouldReturnCategory() {

        Category category = new Category();

        category.setId(1L);
        category.setName("Electronics");


        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));


        CategoryResponseDTO result =
                categoryService.getCategoryById(1L);


        assertEquals(1L, result.getId());
        assertEquals("Electronics", result.getName());


        verify(categoryRepository).findById(1L);
    }


    // Test 3: Get Category By Id - Not Found
    @Test
    void getCategoryById_shouldThrowExceptionWhenCategoryNotFound() {

        when(categoryRepository.findById(999L))
                .thenReturn(Optional.empty());


        assertThrows(
                RuntimeException.class,
                () -> categoryService.getCategoryById(999L)
        );


        verify(categoryRepository).findById(999L);
    }


    // Test 4: Delete Category - Success
    @Test
    void deleteCategory_shouldDeleteCategory() {

        when(categoryRepository.existsById(1L))
                .thenReturn(true);


        categoryService.deleteCategory(1L);


        verify(categoryRepository).existsById(1L);

        verify(categoryRepository).deleteById(1L);
    }


    // Test 5: Delete Category - Not Found
    @Test
    void deleteCategory_shouldThrowExceptionWhenCategoryNotFound() {

        when(categoryRepository.existsById(999L))
                .thenReturn(false);


        assertThrows(
                RuntimeException.class,
                () -> categoryService.deleteCategory(999L)
        );


        verify(categoryRepository).existsById(999L);
    }
}