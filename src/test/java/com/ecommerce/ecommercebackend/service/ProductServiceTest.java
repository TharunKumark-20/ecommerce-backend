package com.ecommerce.ecommercebackend.service;

import com.ecommerce.ecommercebackend.dto.ProductResponseDTO;
import com.ecommerce.ecommercebackend.entity.Category;
import com.ecommerce.ecommercebackend.entity.Product;
import com.ecommerce.ecommercebackend.exception.ResourceNotFoundException;
import com.ecommerce.ecommercebackend.repository.CategoryRepository;
import com.ecommerce.ecommercebackend.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;


    // Test 1: Get Product By Id - Success
    @Test
    void getProductById_shouldReturnProduct() {

        Category category = new Category();

        category.setId(1L);
        category.setName("Electronics");

        Product product = new Product();

        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(50000.0);
        product.setDescription("Gaming Laptop");
        product.setImageUrl(null);
        product.setStock(10);
        product.setCategory(category);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        ProductResponseDTO result =
                productService.getProductById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Laptop", result.getName());
        assertEquals(50000.0, result.getPrice());
        assertEquals("Gaming Laptop", result.getDescription());
        assertEquals(10, result.getStock());
        assertEquals(1L, result.getCategoryId());
        assertEquals("Electronics", result.getCategoryName());

        verify(productRepository).findById(1L);
    }


    // Test 2: Get Product By Id - Product Not Found
    @Test
    void getProductById_shouldThrowExceptionWhenProductNotFound() {

        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.getProductById(999L)
        );

        verify(productRepository).findById(999L);
    }


    // Test 3: Delete Product - Success
    @Test
    void deleteProduct_shouldDeleteProduct() {

        when(productRepository.existsById(1L))
                .thenReturn(true);

        productService.deleteProduct(1L);

        verify(productRepository).existsById(1L);

        verify(productRepository).deleteById(1L);
    }


    // Test 4: Delete Product - Product Not Found
    @Test
    void deleteProduct_shouldThrowExceptionWhenProductNotFound() {

        when(productRepository.existsById(999L))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.deleteProduct(999L)
        );

        verify(productRepository).existsById(999L);
    }
}