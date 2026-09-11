package com.ecommerce.ecommercebackend.dto;

import com.ecommerce.ecommercebackend.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {

    private Long orderId;

    private Long userId;

    private LocalDateTime orderDate;

    private Double totalAmount;

    private OrderStatus status;

    private List<OrderItemResponseDTO> items;
}