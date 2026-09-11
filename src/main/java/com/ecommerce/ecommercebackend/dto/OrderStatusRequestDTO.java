package com.ecommerce.ecommercebackend.dto;

import com.ecommerce.ecommercebackend.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderStatusRequestDTO {

    @NotNull(message = "Order status is required")
    private OrderStatus status;

}