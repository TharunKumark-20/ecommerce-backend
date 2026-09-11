package com.ecommerce.ecommercebackend.repository;

import com.ecommerce.ecommercebackend.entity.Order;
import com.ecommerce.ecommercebackend.entity.OrderStatus;
import com.ecommerce.ecommercebackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Get Orders By User
    List<Order> findByUser(User user);

    // Count Orders By Status
    long countByStatus(OrderStatus status);

    // Calculate Total Revenue
    @Query("""
            SELECT COALESCE(SUM(o.totalAmount), 0)
            FROM Order o
            WHERE o.status <> com.ecommerce.ecommercebackend.entity.OrderStatus.CANCELLED
            """)
    Double getTotalRevenue();
}