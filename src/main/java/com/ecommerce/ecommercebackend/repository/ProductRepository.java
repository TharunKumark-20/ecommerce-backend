package com.ecommerce.ecommercebackend.repository;

import com.ecommerce.ecommercebackend.entity.Category;
import com.ecommerce.ecommercebackend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Search by Product Name
    List<Product> findByNameContainingIgnoreCase(String keyword);

    // Search by Category
    List<Product> findByCategory(Category category);

    // Search by Product Name and Category
    List<Product> findByNameContainingIgnoreCaseAndCategory(
            String keyword,
            Category category
    );

}