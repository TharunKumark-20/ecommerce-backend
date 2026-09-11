package com.ecommerce.ecommercebackend.controller;

import com.ecommerce.ecommercebackend.dto.OrderResponseDTO;
import com.ecommerce.ecommercebackend.entity.Order;
import com.ecommerce.ecommercebackend.entity.OrderStatus;
import com.ecommerce.ecommercebackend.service.CustomUserDetailsService;
import com.ecommerce.ecommercebackend.service.JwtService;
import com.ecommerce.ecommercebackend.service.OrderService;

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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {


    @Autowired
    private MockMvc mockMvc;


    @MockitoBean
    private OrderService orderService;


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
    // TEST 1 : CREATE ORDER
    // =========================================================

    @Test
    void createOrder_shouldCreateOrder()
            throws Exception {

        Order savedOrder = new Order();

        savedOrder.setId(1L);
        savedOrder.setTotalAmount(50000.0);
        savedOrder.setStatus(OrderStatus.PENDING);


        when(orderService.saveOrder(any()))
                .thenReturn(savedOrder);


        String json = """
                {
                    "userId": 1
                }
                """;


        mockMvc.perform(
                        post("/orders")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.totalAmount")
                                .value(50000.0)
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("PENDING")
                );


        verify(orderService)
                .saveOrder(any());
    }


    // =========================================================
    // TEST 2 : GET ALL ORDERS
    // =========================================================

    @Test
    void getAllOrders_shouldReturnOrders()
            throws Exception {

        OrderResponseDTO order1 =
                new OrderResponseDTO(
                        1L,
                        1L,
                        null,
                        50000.0,
                        OrderStatus.PENDING,
                        List.of()
                );


        OrderResponseDTO order2 =
                new OrderResponseDTO(
                        2L,
                        2L,
                        null,
                        75000.0,
                        OrderStatus.CONFIRMED,
                        List.of()
                );


        when(orderService.getAllOrders())
                .thenReturn(
                        List.of(
                                order1,
                                order2
                        )
                );


        mockMvc.perform(
                        get("/orders")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].orderId")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].userId")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].totalAmount")
                                .value(50000.0)
                )
                .andExpect(
                        jsonPath("$[0].status")
                                .value("PENDING")
                )
                .andExpect(
                        jsonPath("$[1].orderId")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[1].userId")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[1].status")
                                .value("CONFIRMED")
                );


        verify(orderService)
                .getAllOrders();
    }


    // =========================================================
    // TEST 3 : GET ORDER BY ID
    // =========================================================

    @Test
    void getOrderById_shouldReturnOrder()
            throws Exception {

        OrderResponseDTO order =
                new OrderResponseDTO(
                        1L,
                        1L,
                        null,
                        50000.0,
                        OrderStatus.PENDING,
                        List.of()
                );


        when(orderService.getOrderById(1L))
                .thenReturn(order);


        mockMvc.perform(
                        get("/orders/1")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.orderId")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.userId")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.totalAmount")
                                .value(50000.0)
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("PENDING")
                );


        verify(orderService)
                .getOrderById(1L);
    }


    // =========================================================
    // TEST 4 : GET ORDERS BY USER
    // =========================================================

    @Test
    void getOrdersByUser_shouldReturnOrders()
            throws Exception {

        OrderResponseDTO order =
                new OrderResponseDTO(
                        1L,
                        1L,
                        null,
                        50000.0,
                        OrderStatus.CONFIRMED,
                        List.of()
                );


        when(orderService.getOrdersByUser(1L))
                .thenReturn(List.of(order));


        mockMvc.perform(
                        get("/orders/user/1")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].orderId")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].userId")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].totalAmount")
                                .value(50000.0)
                )
                .andExpect(
                        jsonPath("$[0].status")
                                .value("CONFIRMED")
                );


        verify(orderService)
                .getOrdersByUser(1L);
    }


    // =========================================================
    // TEST 5 : CANCEL ORDER
    // =========================================================

    @Test
    void cancelOrder_shouldCancelOrder()
            throws Exception {

        Order cancelledOrder = new Order();

        cancelledOrder.setId(1L);
        cancelledOrder.setTotalAmount(50000.0);
        cancelledOrder.setStatus(
                OrderStatus.CANCELLED
        );


        when(orderService.cancelOrder(1L))
                .thenReturn(cancelledOrder);


        mockMvc.perform(
                        put("/orders/cancel/1")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.totalAmount")
                                .value(50000.0)
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("CANCELLED")
                );


        verify(orderService)
                .cancelOrder(1L);
    }


    // =========================================================
    // TEST 6 : UPDATE ORDER STATUS
    // =========================================================

    @Test
    void updateOrderStatus_shouldUpdateStatus()
            throws Exception {

        Order updatedOrder = new Order();

        updatedOrder.setId(1L);
        updatedOrder.setTotalAmount(50000.0);
        updatedOrder.setStatus(
                OrderStatus.SHIPPED
        );


        when(
                orderService.updateOrderStatus(
                        eq(1L),
                        any()
                )
        ).thenReturn(updatedOrder);


        String json = """
                {
                    "status": "SHIPPED"
                }
                """;


        mockMvc.perform(
                        put("/orders/status/1")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.totalAmount")
                                .value(50000.0)
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("SHIPPED")
                );


        verify(orderService)
                .updateOrderStatus(
                        eq(1L),
                        any()
                );
    }


    // =========================================================
    // TEST 7 : DELETE ORDER
    // =========================================================

    @Test
    void deleteOrder_shouldDeleteOrder()
            throws Exception {

        doNothing()
                .when(orderService)
                .deleteOrder(1L);


        mockMvc.perform(
                        delete("/orders/1")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .value(
                                        "Order deleted successfully"
                                )
                );


        verify(orderService)
                .deleteOrder(1L);
    }
}