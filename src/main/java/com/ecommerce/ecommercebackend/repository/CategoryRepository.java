package com.ecommerce.ecommercebackend.repository;

import com.ecommerce.ecommercebackend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}