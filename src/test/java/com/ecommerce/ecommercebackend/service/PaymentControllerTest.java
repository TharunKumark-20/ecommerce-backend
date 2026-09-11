package com.ecommerce.ecommercebackend.controller;

import com.ecommerce.ecommercebackend.dto.PaymentResponseDTO;
import com.ecommerce.ecommercebackend.entity.PaymentMethod;
import com.ecommerce.ecommercebackend.entity.PaymentStatus;
import com.ecommerce.ecommercebackend.service.CustomUserDetailsService;
import com.ecommerce.ecommercebackend.service.JwtService;
import com.ecommerce.ecommercebackend.service.PaymentService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {


    @Autowired
    private MockMvc mockMvc;


    @MockitoBean
    private PaymentService paymentService;


    @MockitoBean
    private JwtService jwtService;


    @MockitoBean
    private CustomUserDetailsService userDetailsService;


    // =========================================================
    // SECURITY SETUP
    // =========================================================

    @BeforeEach
    void setUpSecurityContext() {

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "testadmin",
                        null,
                        List.of(
                                new SimpleGrantedAuthority(
                                        "ROLE_ADMIN"
                                )
                        )
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);
    }


    @AfterEach
    void clearSecurityContext() {

        SecurityContextHolder.clearContext();
    }


    // =========================================================
    // TEST 1 : MAKE PAYMENT
    // =========================================================

    @Test
    void makePayment_shouldCreatePayment()
            throws Exception {

        PaymentResponseDTO payment =
                new PaymentResponseDTO(
                        1L,
                        1L,
                        50000.0,
                        PaymentMethod.CREDIT_CARD,
                        PaymentStatus.SUCCESS,
                        LocalDateTime.now()
                );


        when(paymentService.makePayment(any()))
                .thenReturn(payment);


        String json = """
                {
                    "orderId": 1,
                    "paymentMethod": "CREDIT_CARD"
                }
                """;


        mockMvc.perform(
                        post("/payments")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.paymentId")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.orderId")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.amount")
                                .value(50000.0)
                )
                .andExpect(
                        jsonPath("$.paymentMethod")
                                .value("CREDIT_CARD")
                )
                .andExpect(
                        jsonPath("$.paymentStatus")
                                .value("SUCCESS")
                );


        verify(paymentService)
                .makePayment(any());
    }


    // =========================================================
    // TEST 2 : GET PAYMENT BY ID
    // =========================================================

    @Test
    void getPayment_shouldReturnPayment()
            throws Exception {

        PaymentResponseDTO payment =
                new PaymentResponseDTO(
                        1L,
                        1L,
                        50000.0,
                        PaymentMethod.CREDIT_CARD,
                        PaymentStatus.SUCCESS,
                        LocalDateTime.now()
                );


        when(paymentService.getPayment(1L))
                .thenReturn(payment);


        mockMvc.perform(
                        get("/payments/1")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.paymentId")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.orderId")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.amount")
                                .value(50000.0)
                )
                .andExpect(
                        jsonPath("$.paymentMethod")
                                .value("CREDIT_CARD")
                )
                .andExpect(
                        jsonPath("$.paymentStatus")
                                .value("SUCCESS")
                );


        verify(paymentService)
                .getPayment(1L);
    }
}