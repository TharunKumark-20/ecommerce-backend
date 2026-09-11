package com.ecommerce.ecommercebackend.controller;

import com.ecommerce.ecommercebackend.dto.PageResponseDTO;
import com.ecommerce.ecommercebackend.dto.ProductRequestDTO;
import com.ecommerce.ecommercebackend.dto.ProductResponseDTO;
import com.ecommerce.ecommercebackend.entity.Product;
import com.ecommerce.ecommercebackend.service.FileStorageService;
import com.ecommerce.ecommercebackend.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    private final FileStorageService fileStorageService;


    // =========================================================
    // Create Product with Image Upload
    // =========================================================
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Product> createProduct(

            @Valid
            @ModelAttribute ProductRequestDTO productRequestDTO,

            @RequestParam("image") MultipartFile image

    ) throws IOException {

        String imageUrl =
                fileStorageService.saveFile(image);

        productRequestDTO.setImageUrl(
                imageUrl
        );

        Product savedProduct =
                productService.saveProduct(
                        productRequestDTO
                );

        return new ResponseEntity<>(
                savedProduct,
                HttpStatus.CREATED
        );
    }


    // =========================================================
    // Get All Products with Pagination & Sorting
    // =========================================================
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public ResponseEntity<PageResponseDTO<ProductResponseDTO>> getAllProducts(

            @RequestParam(defaultValue = "0")
            int pageNumber,

            @RequestParam(defaultValue = "5")
            int pageSize,

            @RequestParam(defaultValue = "id")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String sortDir

    ) {

        return ResponseEntity.ok(
                productService.getAllProducts(
                        pageNumber,
                        pageSize,
                        sortBy,
                        sortDir
                )
        );
    }


    // =========================================================
    // Search Products by Name
    // =========================================================
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDTO>> searchProducts(

            @RequestParam String keyword

    ) {

        return ResponseEntity.ok(
                productService.searchProducts(
                        keyword
                )
        );
    }


    // =========================================================
    // Get Products by Category
    // =========================================================
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByCategory(

            @PathVariable Long categoryId

    ) {

        return ResponseEntity.ok(
                productService.getProductsByCategory(
                        categoryId
                )
        );
    }


    // =========================================================
    // Search by Name and Category
    // =========================================================
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/search/category")
    public ResponseEntity<List<ProductResponseDTO>> searchProductsByCategory(

            @RequestParam String keyword,

            @RequestParam Long categoryId

    ) {

        return ResponseEntity.ok(
                productService.searchProductsByCategory(
                        keyword,
                        categoryId
                )
        );
    }


    // =========================================================
    // Get Product By Id
    // =========================================================
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(

            @PathVariable Long id

    ) {

        return ResponseEntity.ok(
                productService.getProductById(
                        id
                )
        );
    }


    // =========================================================
    // Update Product with Optional Image Upload
    // =========================================================
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(consumes = "multipart/form-data")
    public ResponseEntity<Product> updateProduct(

            @RequestParam Long id,

            @RequestParam String name,

            @RequestParam Double price,

            @RequestParam String description,

            @RequestParam Integer stock,

            // Category is optional because some existing
            // products may not have a category.
            @RequestParam(required = false) Long categoryId,

            // Image is optional.
            @RequestParam(required = false)
            MultipartFile image

    ) throws IOException {

        String imageUrl = null;

        // Save new image only when one was selected.
        if (image != null &&
                !image.isEmpty()) {

            imageUrl =
                    fileStorageService.saveFile(
                            image
                    );
        }

        Product updatedProduct =
                productService.updateProduct(
                        id,
                        name,
                        price,
                        description,
                        stock,
                        categoryId,
                        imageUrl
                );

        return ResponseEntity.ok(
                updatedProduct
        );
    }


    // =========================================================
    // Delete Product
    // =========================================================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(

            @PathVariable Long id

    ) {

        productService.deleteProduct(id);

        return ResponseEntity.noContent()
                .build();
    }
}