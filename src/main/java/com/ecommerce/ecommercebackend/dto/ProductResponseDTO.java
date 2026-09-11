package com.ecommerce.ecommercebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {

    private Long id;

    private String name;

    private Double price;

    private String description;

    private String imageUrl;

    private Integer stock;

    private Long categoryId;

    private String categoryName;
}