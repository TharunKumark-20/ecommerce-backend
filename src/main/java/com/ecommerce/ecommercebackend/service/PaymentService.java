package com.ecommerce.ecommercebackend.service;

import com.ecommerce.ecommercebackend.dto.PaymentRequestDTO;
import com.ecommerce.ecommercebackend.dto.PaymentResponseDTO;
import com.ecommerce.ecommercebackend.entity.Order;
import com.ecommerce.ecommercebackend.entity.Payment;
import com.ecommerce.ecommercebackend.entity.PaymentStatus;
import com.ecommerce.ecommercebackend.exception.ResourceNotFoundException;
import com.ecommerce.ecommercebackend.repository.OrderRepository;
import com.ecommerce.ecommercebackend.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    // Make Payment
    public PaymentResponseDTO makePayment(
            PaymentRequestDTO paymentRequestDTO) {

        Order order = orderRepository.findById(
                paymentRequestDTO.getOrderId()
        ).orElseThrow(() ->
                new ResourceNotFoundException("Order not found")
        );

        if (paymentRepository.findByOrderId(order.getId()).isPresent()) {
            throw new RuntimeException(
                    "Payment already exists for this order."
            );
        }

        Payment payment = new Payment();

        payment.setOrder(order);

        payment.setAmount(order.getTotalAmount());

        payment.setPaymentMethod(
                paymentRequestDTO.getPaymentMethod()
        );

        payment.setPaymentStatus(PaymentStatus.SUCCESS);

        payment.setPaymentDate(LocalDateTime.now());

        Payment savedPayment =
                paymentRepository.save(payment);

        return convertToDTO(savedPayment);
    }

    // Get Payment By Id
    public PaymentResponseDTO getPayment(Long paymentId) {

        Payment payment =
                paymentRepository.findById(paymentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Payment not found"
                                )
                        );

        return convertToDTO(payment);
    }

    // Entity -> DTO
    private PaymentResponseDTO convertToDTO(
            Payment payment) {

        return new PaymentResponseDTO(

                payment.getId(),

                payment.getOrder().getId(),

                payment.getAmount(),

                payment.getPaymentMethod(),

                payment.getPaymentStatus(),

                payment.getPaymentDate()

        );
    }
}