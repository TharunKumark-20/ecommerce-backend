package com.ecommerce.ecommercebackend.controller;

import com.ecommerce.ecommercebackend.dto.CartResponseDTO;
import com.ecommerce.ecommercebackend.entity.Cart;
import com.ecommerce.ecommercebackend.service.CartService;
import com.ecommerce.ecommercebackend.service.CustomUserDetailsService;
import com.ecommerce.ecommercebackend.service.JwtService;

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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
class CartControllerTest {


    @Autowired
    private MockMvc mockMvc;


    @MockitoBean
    private CartService cartService;


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
    // TEST 1 : CREATE CART
    // =========================================================

    @Test
    void createCart_shouldCreateCart()
            throws Exception {

        Cart savedCart = new Cart();

        savedCart.setId(1L);


        when(cartService.saveCart(any()))
                .thenReturn(savedCart);


        String json = """
                {
                    "userId": 1
                }
                """;


        mockMvc.perform(
                        post("/carts")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                );


        verify(cartService)
                .saveCart(any());
    }


    // =========================================================
    // TEST 2 : GET ALL CARTS
    // =========================================================

    @Test
    void getAllCarts_shouldReturnCarts()
            throws Exception {

        CartResponseDTO cart1 =
                new CartResponseDTO(
                        1L,
                        1L,
                        List.of(),
                        0.0
                );


        CartResponseDTO cart2 =
                new CartResponseDTO(
                        2L,
                        2L,
                        List.of(),
                        0.0
                );


        when(cartService.getAllCarts())
                .thenReturn(
                        List.of(
                                cart1,
                                cart2
                        )
                );


        mockMvc.perform(
                        get("/carts")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].cartId")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].userId")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].totalAmount")
                                .value(0.0)
                )
                .andExpect(
                        jsonPath("$[1].cartId")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[1].userId")
                                .value(2)
                );
    }


    // =========================================================
    // TEST 3 : GET CART BY ID
    // =========================================================

    @Test
    void getCartById_shouldReturnCart()
            throws Exception {

        CartResponseDTO cart =
                new CartResponseDTO(
                        1L,
                        1L,
                        List.of(),
                        0.0
                );


        when(cartService.getCartById(1L))
                .thenReturn(cart);


        mockMvc.perform(
                        get("/carts/1")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.cartId")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.userId")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.totalAmount")
                                .value(0.0)
                );


        verify(cartService)
                .getCartById(1L);
    }


    // =========================================================
    // TEST 4 : GET CART BY USER
    // =========================================================

    @Test
    void getCartByUser_shouldReturnCart()
            throws Exception {

        CartResponseDTO cart =
                new CartResponseDTO(
                        1L,
                        1L,
                        List.of(),
                        0.0
                );


        when(cartService.getCartByUser(1L))
                .thenReturn(cart);


        mockMvc.perform(
                        get("/carts/user/1")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.cartId")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.userId")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.totalAmount")
                                .value(0.0)
                );


        verify(cartService)
                .getCartByUser(1L);
    }


    // =========================================================
    // TEST 5 : DELETE CART
    // =========================================================

    @Test
    void deleteCart_shouldDeleteCart()
            throws Exception {

        doNothing()
                .when(cartService)
                .deleteCart(1L);


        mockMvc.perform(
                        delete("/carts/1")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .value(
                                        "Cart deleted successfully"
                                )
                );


        verify(cartService)
                .deleteCart(1L);
    }
}