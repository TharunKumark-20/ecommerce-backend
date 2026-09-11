package com.ecommerce.ecommercebackend.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class CategoryRequestDTO {

    @NotBlank(message = "Category name is required")
    private String name;
}