package com.ecommerce.ecommercebackend.service;

import com.ecommerce.ecommercebackend.dto.PaymentRequestDTO;
import com.ecommerce.ecommercebackend.dto.PaymentResponseDTO;
import com.ecommerce.ecommercebackend.entity.Order;
import com.ecommerce.ecommercebackend.entity.Payment;
import com.ecommerce.ecommercebackend.entity.PaymentMethod;
import com.ecommerce.ecommercebackend.entity.PaymentStatus;
import com.ecommerce.ecommercebackend.repository.OrderRepository;
import com.ecommerce.ecommercebackend.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentService paymentService;


    // Test 1: Make Payment Successfully
    @Test
    void makePayment_shouldCreatePaymentSuccessfully() {

        PaymentRequestDTO request = new PaymentRequestDTO();

        request.setOrderId(1L);
        request.setPaymentMethod(PaymentMethod.CREDIT_CARD);


        Order order = new Order();

        order.setId(1L);
        order.setTotalAmount(50000.0);


        Payment savedPayment = new Payment();

        savedPayment.setId(100L);
        savedPayment.setOrder(order);
        savedPayment.setAmount(50000.0);
        savedPayment.setPaymentMethod(
                PaymentMethod.CREDIT_CARD
        );
        savedPayment.setPaymentStatus(
                PaymentStatus.SUCCESS
        );


        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(paymentRepository.findByOrderId(1L))
                .thenReturn(Optional.empty());

        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(savedPayment);


        PaymentResponseDTO result =
                paymentService.makePayment(request);


        assertEquals(100L, result.getPaymentId());
        assertEquals(1L, result.getOrderId());
        assertEquals(50000.0, result.getAmount());
        assertEquals(
                PaymentMethod.CREDIT_CARD,
                result.getPaymentMethod()
        );
        assertEquals(
                PaymentStatus.SUCCESS,
                result.getPaymentStatus()
        );


        verify(orderRepository).findById(1L);
        verify(paymentRepository).findByOrderId(1L);
        verify(paymentRepository).save(any(Payment.class));
    }


    // Test 2: Order Not Found
    @Test
    void makePayment_shouldThrowExceptionWhenOrderNotFound() {

        PaymentRequestDTO request = new PaymentRequestDTO();

        request.setOrderId(999L);
        request.setPaymentMethod(PaymentMethod.CREDIT_CARD);


        when(orderRepository.findById(999L))
                .thenReturn(Optional.empty());


        assertThrows(
                RuntimeException.class,
                () -> paymentService.makePayment(request)
        );


        verify(orderRepository).findById(999L);
    }


    // Test 3: Payment Already Exists
    @Test
    void makePayment_shouldThrowExceptionWhenPaymentAlreadyExists() {

        PaymentRequestDTO request = new PaymentRequestDTO();

        request.setOrderId(1L);
        request.setPaymentMethod(PaymentMethod.CREDIT_CARD);


        Order order = new Order();

        order.setId(1L);
        order.setTotalAmount(50000.0);


        Payment existingPayment = new Payment();

        existingPayment.setId(100L);
        existingPayment.setOrder(order);


        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(paymentRepository.findByOrderId(1L))
                .thenReturn(Optional.of(existingPayment));


        assertThrows(
                RuntimeException.class,
                () -> paymentService.makePayment(request)
        );


        verify(orderRepository).findById(1L);
        verify(paymentRepository).findByOrderId(1L);
    }


    // Test 4: Get Payment Successfully
    @Test
    void getPayment_shouldReturnPayment() {

        Order order = new Order();

        order.setId(1L);


        Payment payment = new Payment();

        payment.setId(100L);
        payment.setOrder(order);
        payment.setAmount(50000.0);
        payment.setPaymentMethod(
                PaymentMethod.CREDIT_CARD
        );
        payment.setPaymentStatus(
                PaymentStatus.SUCCESS
        );


        when(paymentRepository.findById(100L))
                .thenReturn(Optional.of(payment));


        PaymentResponseDTO result =
                paymentService.getPayment(100L);


        assertEquals(100L, result.getPaymentId());
        assertEquals(1L, result.getOrderId());
        assertEquals(50000.0, result.getAmount());
        assertEquals(
                PaymentMethod.CREDIT_CARD,
                result.getPaymentMethod()
        );
        assertEquals(
                PaymentStatus.SUCCESS,
                result.getPaymentStatus()
        );


        verify(paymentRepository).findById(100L);
    }


    // Test 5: Payment Not Found
    @Test
    void getPayment_shouldThrowExceptionWhenPaymentNotFound() {

        when(paymentRepository.findById(999L))
                .thenReturn(Optional.empty());


        assertThrows(
                RuntimeException.class,
                () -> paymentService.getPayment(999L)
        );


        verify(paymentRepository).findById(999L);
    }
}