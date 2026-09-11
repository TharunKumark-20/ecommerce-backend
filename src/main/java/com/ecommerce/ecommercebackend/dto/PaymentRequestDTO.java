package com.ecommerce.ecommercebackend.dto;

import com.ecommerce.ecommercebackend.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequestDTO {

    @NotNull
    private Long orderId;

    @NotNull
    private PaymentMethod paymentMethod;

}