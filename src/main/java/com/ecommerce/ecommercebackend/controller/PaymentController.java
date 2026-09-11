package com.ecommerce.ecommercebackend.controller;

import com.ecommerce.ecommercebackend.dto.PaymentRequestDTO;
import com.ecommerce.ecommercebackend.dto.PaymentResponseDTO;
import com.ecommerce.ecommercebackend.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // Make Payment
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping
    public ResponseEntity<PaymentResponseDTO> makePayment(
            @Valid @RequestBody PaymentRequestDTO paymentRequestDTO) {

        PaymentResponseDTO paymentResponse =
                paymentService.makePayment(paymentRequestDTO);

        return new ResponseEntity<>(
                paymentResponse,
                HttpStatus.CREATED
        );
    }

    // Get Payment By Id
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDTO> getPayment(
            @PathVariable Long paymentId) {

        return ResponseEntity.ok(
                paymentService.getPayment(paymentId)
        );
    }
}
    