package com.ecommerce.ecommercebackend.service;

import com.ecommerce.ecommercebackend.dto.CategoryRequestDTO;
import com.ecommerce.ecommercebackend.dto.CategoryResponseDTO;
import com.ecommerce.ecommercebackend.entity.Category;
import com.ecommerce.ecommercebackend.entity.Product;
import com.ecommerce.ecommercebackend.exception.ResourceNotFoundException;
import com.ecommerce.ecommercebackend.repository.CategoryRepository;
import com.ecommerce.ecommercebackend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final ProductRepository productRepository;


    // =========================================================
    // Create Category
    // =========================================================
    public Category saveCategory(
            CategoryRequestDTO categoryRequestDTO) {

        Category category = new Category();

        category.setName(
                categoryRequestDTO.getName()
        );

        return categoryRepository.save(category);
    }


    // =========================================================
    // Get All Categories
    // =========================================================
    public List<CategoryResponseDTO> getAllCategories() {

        return categoryRepository.findAll()

                .stream()

                .map(this::convertToDTO)

                .toList();
    }


    // =========================================================
    // Get Category By Id
    // =========================================================
    public CategoryResponseDTO getCategoryById(Long id) {

        Category category =
                categoryRepository.findById(id)

                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Category not found"
                                )
                        );

        return convertToDTO(category);
    }


    // =========================================================
    // Update Category
    // =========================================================
    public Category updateCategory(
            Long id,
            CategoryRequestDTO categoryRequestDTO) {

        Category category =
                categoryRepository.findById(id)

                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Category not found"
                                )
                        );

        category.setName(
                categoryRequestDTO.getName()
        );

        return categoryRepository.save(category);
    }


    // =========================================================
    // Delete Category
    // =========================================================
    public void deleteCategory(Long id) {

        Category category =
                categoryRepository.findById(id)

                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Category not found"
                                )
                        );


        // Check whether any products use this category.
        List<Product> products =
                productRepository.findByCategory(category);


        if (!products.isEmpty()) {

            throw new IllegalStateException(
                    "Category cannot be deleted because it is being used by one or more products."
            );
        }


        try {

            categoryRepository.delete(category);

        } catch (DataIntegrityViolationException exception) {

            throw new IllegalStateException(
                    "Category cannot be deleted because it is being used by one or more products."
            );
        }
    }


    // =========================================================
    // Entity -> DTO Conversion
    // =========================================================
    private CategoryResponseDTO convertToDTO(
            Category category) {

        return new CategoryResponseDTO(

                category.getId(),

                category.getName()

        );
    }

}