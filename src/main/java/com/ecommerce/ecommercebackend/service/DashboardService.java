package com.ecommerce.ecommercebackend.service;

import com.ecommerce.ecommercebackend.dto.DashboardResponseDTO;
import com.ecommerce.ecommercebackend.entity.OrderStatus;
import com.ecommerce.ecommercebackend.repository.OrderRepository;
import com.ecommerce.ecommercebackend.repository.ProductRepository;
import com.ecommerce.ecommercebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;


    public DashboardResponseDTO getDashboardData() {

        Long totalUsers = userRepository.count();

        Long totalProducts = productRepository.count();

        Long totalOrders = orderRepository.count();


        Double totalRevenue = orderRepository.getTotalRevenue();


        Long pendingOrders =
                orderRepository.countByStatus(OrderStatus.PENDING);


        Long deliveredOrders =
                orderRepository.countByStatus(OrderStatus.DELIVERED);


        Long cancelledOrders =
                orderRepository.countByStatus(OrderStatus.CANCELLED);


        return new DashboardResponseDTO(
                totalUsers,
                totalProducts,
                totalOrders,
                totalRevenue,
                pendingOrders,
                deliveredOrders,
                cancelledOrders
        );
    }
}