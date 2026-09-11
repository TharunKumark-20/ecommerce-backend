package com.ecommerce.ecommercebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO {

    private String accessToken;

    private String refreshToken;

    private Long userId;
}