package com.ecommerce.ecommercebackend.service;

import com.ecommerce.ecommercebackend.dto.PageResponseDTO;
import com.ecommerce.ecommercebackend.dto.ProductRequestDTO;
import com.ecommerce.ecommercebackend.dto.ProductResponseDTO;
import com.ecommerce.ecommercebackend.entity.Category;
import com.ecommerce.ecommercebackend.entity.CartItem;
import com.ecommerce.ecommercebackend.entity.Product;
import com.ecommerce.ecommercebackend.exception.ResourceNotFoundException;
import com.ecommerce.ecommercebackend.repository.CartItemRepository;
import com.ecommerce.ecommercebackend.repository.CategoryRepository;
import com.ecommerce.ecommercebackend.repository.OrderItemRepository;
import com.ecommerce.ecommercebackend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final Logger logger =
            LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    private final CartItemRepository cartItemRepository;

    private final OrderItemRepository orderItemRepository;


    // =========================================================
    // Pagination + Sorting
    // =========================================================
    public PageResponseDTO<ProductResponseDTO> getAllProducts(
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDir) {

        logger.info(
                "Fetching products. Page: {}, Size: {}, SortBy: {}, Direction: {}",
                pageNumber,
                pageSize,
                sortBy,
                sortDir
        );

        Sort sort =
                sortDir.equalsIgnoreCase("asc")
                        ? Sort.by(sortBy).ascending()
                        : Sort.by(sortBy).descending();

        Pageable pageable =
                PageRequest.of(
                        pageNumber,
                        pageSize,
                        sort
                );

        Page<Product> page =
                productRepository.findAll(pageable);

        List<ProductResponseDTO> products =
                page.getContent()
                        .stream()
                        .map(this::convertToDTO)
                        .toList();

        return new PageResponseDTO<>(
                products,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }


    // =========================================================
    // Create Product
    // =========================================================
    public Product saveProduct(
            ProductRequestDTO productRequestDTO) {

        logger.info(
                "Creating product: {}",
                productRequestDTO.getName()
        );

        Category category =
                categoryRepository
                        .findById(
                                productRequestDTO.getCategoryId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Category not found"
                                )
                        );

        Product product =
                new Product();

        product.setName(
                productRequestDTO.getName()
        );

        product.setPrice(
                productRequestDTO.getPrice()
        );

        product.setDescription(
                productRequestDTO.getDescription()
        );

        product.setStock(
                productRequestDTO.getStock()
        );

        product.setImageUrl(
                productRequestDTO.getImageUrl()
        );

        product.setCategory(
                category
        );

        Product savedProduct =
                productRepository.save(product);

        logger.info(
                "Product created successfully with id: {}",
                savedProduct.getId()
        );

        return savedProduct;
    }


    // =========================================================
    // Get Product By Id
    // =========================================================
    public ProductResponseDTO getProductById(
            Long id) {

        Product product =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found"
                                )
                        );

        return convertToDTO(product);
    }


    // =========================================================
    // Update Product
    // =========================================================
    @Transactional
    public Product updateProduct(
            Long id,
            String name,
            Double price,
            String description,
            Integer stock,
            Long categoryId,
            String imageUrl) {

        logger.info(
                "Updating product id: {}",
                id
        );

        // Find existing product
        Product product =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found"
                                )
                        );

        // Update basic details
        product.setName(name);
        product.setPrice(price);
        product.setDescription(description);
        product.setStock(stock);

        // Update category only when a category was supplied
        if (categoryId != null) {

            Category category =
                    categoryRepository.findById(categoryId)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Category not found"
                                    )
                            );

            product.setCategory(category);
        }

        // Update image only when a new image was uploaded
        // Otherwise keep the existing image
        if (imageUrl != null && !imageUrl.isBlank()) {
            product.setImageUrl(imageUrl);
        }

        Product updatedProduct =
                productRepository.save(product);

        logger.info(
                "Product updated successfully with id: {}",
                id
        );

        return updatedProduct;
    }
    // =========================================================
// Legacy Update Method
// =========================================================
// Kept for existing tests and backward compatibility.
    public Product updateProduct(Product product) {

        logger.info(
                "Updating product using legacy method. Product id: {}",
                product.getId()
        );

        return productRepository.save(product);
    }


    // =========================================================
    // Delete Product
    // =========================================================
    @Transactional
    public void deleteProduct(Long id) {

        Product product =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found"
                                )
                        );

        // Do not delete products that are already
        // part of an order.
        if (orderItemRepository.existsByProduct(product)) {

            throw new IllegalStateException(
                    "Product cannot be deleted because it is part of an existing order."
            );
        }

        // Remove the product from carts first.
        List<CartItem> cartItems =
                cartItemRepository.findByProduct(product);

        if (!cartItems.isEmpty()) {

            cartItemRepository.deleteAll(cartItems);

            logger.info(
                    "Removed {} cart items for product id: {}",
                    cartItems.size(),
                    id
            );
        }

        // Now the product can safely be deleted.
        productRepository.delete(product);

        logger.info(
                "Product deleted successfully with id: {}",
                id
        );
    }


    // =========================================================
    // Search Products by Name
    // =========================================================
    public List<ProductResponseDTO> searchProducts(
            String keyword) {

        return productRepository
                .findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }


    // =========================================================
    // Products by Category
    // =========================================================
    public List<ProductResponseDTO> getProductsByCategory(
            Long categoryId) {

        Category category =
                categoryRepository.findById(categoryId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Category not found"
                                )
                        );

        return productRepository
                .findByCategory(category)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }


    // =========================================================
    // Search by Name and Category
    // =========================================================
    public List<ProductResponseDTO> searchProductsByCategory(
            String keyword,
            Long categoryId) {

        Category category =
                categoryRepository.findById(categoryId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Category not found"
                                )
                        );

        return productRepository
                .findByNameContainingIgnoreCaseAndCategory(
                        keyword,
                        category
                )
                .stream()
                .map(this::convertToDTO)
                .toList();
    }


    // =========================================================
    // Entity → DTO Conversion
    // =========================================================
    private ProductResponseDTO convertToDTO(
            Product product) {

        Long categoryId = null;

        String categoryName = null;

        if (product.getCategory() != null) {

            categoryId =
                    product.getCategory().getId();

            categoryName =
                    product.getCategory().getName();
        }

        return new ProductResponseDTO(

                product.getId(),

                product.getName(),

                product.getPrice(),

                product.getDescription(),

                product.getImageUrl(),

                product.getStock(),

                categoryId,

                categoryName

        );
    }


}