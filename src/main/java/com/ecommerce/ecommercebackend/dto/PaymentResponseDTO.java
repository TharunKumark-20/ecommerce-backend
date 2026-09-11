package com.ecommerce.ecommercebackend.dto;

import com.ecommerce.ecommercebackend.entity.PaymentMethod;
import com.ecommerce.ecommercebackend.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PaymentResponseDTO {

    private Long paymentId;

    private Long orderId;

    private Double amount;

    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private LocalDateTime paymentDate;

}