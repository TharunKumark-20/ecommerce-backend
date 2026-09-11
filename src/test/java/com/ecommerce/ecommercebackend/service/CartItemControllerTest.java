package com.ecommerce.ecommercebackend.controller;

import com.ecommerce.ecommercebackend.dto.CartItemRequestDTO;
import com.ecommerce.ecommercebackend.entity.CartItem;
import com.ecommerce.ecommercebackend.service.CartItemService;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CartItemController.class)
@AutoConfigureMockMvc(addFilters = false)
class CartItemControllerTest {


    @Autowired
    private MockMvc mockMvc;


    @MockitoBean
    private CartItemService cartItemService;


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
    // TEST 1 : CREATE CART ITEM
    // =========================================================

    @Test
    void createCartItem_shouldCreateCartItem()
            throws Exception {

        CartItem savedCartItem =
                new CartItem();

        savedCartItem.setId(1L);
        savedCartItem.setQuantity(2);


        when(cartItemService.saveCartItem(any()))
                .thenReturn(savedCartItem);


        String json = """
                {
                    "cartId": 1,
                    "productId": 1,
                    "quantity": 2
                }
                """;


        mockMvc.perform(
                        post("/cart-items")
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
                        jsonPath("$.quantity")
                                .value(2)
                );


        verify(cartItemService)
                .saveCartItem(any());
    }


    // =========================================================
    // TEST 2 : GET ALL CART ITEMS
    // =========================================================

    @Test
    void getAllCartItems_shouldReturnCartItems()
            throws Exception {

        CartItem item1 =
                new CartItem();

        item1.setId(1L);
        item1.setQuantity(2);


        CartItem item2 =
                new CartItem();

        item2.setId(2L);
        item2.setQuantity(3);


        when(cartItemService.getAllCartItems())
                .thenReturn(
                        List.of(
                                item1,
                                item2
                        )
                );


        mockMvc.perform(
                        get("/cart-items")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].quantity")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[1].id")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[1].quantity")
                                .value(3)
                );


        verify(cartItemService)
                .getAllCartItems();
    }


    // =========================================================
    // TEST 3 : GET CART ITEM BY ID
    // =========================================================

    @Test
    void getCartItemById_shouldReturnCartItem()
            throws Exception {

        CartItem cartItem =
                new CartItem();

        cartItem.setId(1L);
        cartItem.setQuantity(2);


        when(cartItemService.getCartItemById(1L))
                .thenReturn(
                        Optional.of(cartItem)
                );


        mockMvc.perform(
                        get("/cart-items/1")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.quantity")
                                .value(2)
                );


        verify(cartItemService)
                .getCartItemById(1L);
    }


    // =========================================================
    // TEST 4 : GET CART ITEMS BY CART
    // =========================================================

    @Test
    void getCartItemsByCart_shouldReturnCartItems()
            throws Exception {

        CartItem item1 =
                new CartItem();

        item1.setId(1L);
        item1.setQuantity(2);


        CartItem item2 =
                new CartItem();

        item2.setId(2L);
        item2.setQuantity(3);


        when(
                cartItemService.getCartItemsByCart(1L)
        ).thenReturn(
                List.of(
                        item1,
                        item2
                )
        );


        mockMvc.perform(
                        get("/cart-items/cart/1")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].quantity")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[1].id")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[1].quantity")
                                .value(3)
                );


        verify(cartItemService)
                .getCartItemsByCart(1L);
    }


    // =========================================================
    // TEST 5 : DELETE CART ITEM
    // =========================================================

    @Test
    void deleteCartItem_shouldDeleteCartItem()
            throws Exception {

        doNothing()
                .when(cartItemService)
                .deleteCartItem(1L);


        mockMvc.perform(
                        delete("/cart-items/1")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .value(
                                        "Cart Item deleted successfully"
                                )
                );


        verify(cartItemService)
                .deleteCartItem(1L);
    }
}