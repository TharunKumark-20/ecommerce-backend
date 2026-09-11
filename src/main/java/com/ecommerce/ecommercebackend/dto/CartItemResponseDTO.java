package com.ecommerce.ecommercebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDTO {

    private Long id;

    private Long productId;

    private String productName;

    private Double price;

    private Integer quantity;

    private Double subTotal;
}