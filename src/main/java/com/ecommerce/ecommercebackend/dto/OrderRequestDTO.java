package com.ecommerce.ecommercebackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequestDTO {

    @NotNull(message = "User Id is required")
    private Long userId;

}