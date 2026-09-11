package com.ecommerce.ecommercebackend.dto;

import lombok.Data;

@Data
public class OrderItemRequestDTO {

    private Integer quantity;

    private Long orderId;

    private Long productId;
}