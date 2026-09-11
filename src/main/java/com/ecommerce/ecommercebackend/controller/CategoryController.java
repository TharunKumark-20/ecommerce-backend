package com.ecommerce.ecommercebackend.controller;

import com.ecommerce.ecommercebackend.dto.CategoryRequestDTO;
import com.ecommerce.ecommercebackend.dto.CategoryResponseDTO;
import com.ecommerce.ecommercebackend.entity.Category;
import com.ecommerce.ecommercebackend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;


    // =========================================================
    // Create Category
    // =========================================================
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Category> createCategory(
            @RequestBody CategoryRequestDTO categoryRequestDTO) {

        Category savedCategory =
                categoryService.saveCategory(
                        categoryRequestDTO
                );

        return new ResponseEntity<>(
                savedCategory,
                HttpStatus.CREATED
        );
    }


    // =========================================================
    // Get All Categories
    // =========================================================
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {

        return ResponseEntity.ok(
                categoryService.getAllCategories()
        );
    }


    // =========================================================
    // Get Category By Id
    // =========================================================
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                categoryService.getCategoryById(id)
        );
    }


    // =========================================================
    // Update Category
    // =========================================================
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryRequestDTO categoryRequestDTO) {

        Category updatedCategory =
                categoryService.updateCategory(
                        id,
                        categoryRequestDTO
                );

        return ResponseEntity.ok(
                updatedCategory
        );
    }


    // =========================================================
    // Delete Category
    // =========================================================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(
            @PathVariable Long id) {

        categoryService.deleteCategory(id);

        return ResponseEntity.ok(
                "Category deleted successfully"
        );
    }
}