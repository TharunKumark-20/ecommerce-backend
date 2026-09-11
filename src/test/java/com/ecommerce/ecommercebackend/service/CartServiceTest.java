package com.ecommerce.ecommercebackend.service;

import com.ecommerce.ecommercebackend.dto.CartResponseDTO;
import com.ecommerce.ecommercebackend.entity.Cart;
import com.ecommerce.ecommercebackend.entity.CartItem;
import com.ecommerce.ecommercebackend.entity.Product;
import com.ecommerce.ecommercebackend.entity.User;
import com.ecommerce.ecommercebackend.repository.CartRepository;
import com.ecommerce.ecommercebackend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartService cartService;


    // Test 1: Get All Carts
    @Test
    void getAllCarts_shouldReturnCarts() {

        User user = new User();
        user.setId(1L);

        Product product = new Product();
        product.setId(10L);
        product.setName("Laptop");
        product.setPrice(50000.0);

        CartItem cartItem = new CartItem();
        cartItem.setId(100L);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setCartItems(List.of(cartItem));

        when(cartRepository.findAll())
                .thenReturn(List.of(cart));

        List<CartResponseDTO> result =
                cartService.getAllCarts();

        assertEquals(1, result.size());

        assertEquals(1L, result.get(0).getCartId());
        assertEquals(1L, result.get(0).getUserId());

        assertEquals(1, result.get(0).getItems().size());

        assertEquals(
                10L,
                result.get(0).getItems().get(0).getProductId()
        );

        assertEquals(
                "Laptop",
                result.get(0).getItems().get(0).getProductName()
        );

        assertEquals(
                50000.0,
                result.get(0).getItems().get(0).getPrice()
        );

        assertEquals(
                2,
                result.get(0).getItems().get(0).getQuantity()
        );

        assertEquals(
                100000.0,
                result.get(0).getItems().get(0).getSubTotal()
        );

        assertEquals(
                100000.0,
                result.get(0).getTotalAmount()
        );

        verify(cartRepository).findAll();
    }


    // Test 2: Get Cart By Id - Success
    @Test
    void getCartById_shouldReturnCart() {

        User user = new User();
        user.setId(1L);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setCartItems(new ArrayList<>());

        when(cartRepository.findById(1L))
                .thenReturn(Optional.of(cart));

        CartResponseDTO result =
                cartService.getCartById(1L);

        assertEquals(1L, result.getCartId());
        assertEquals(1L, result.getUserId());
        assertEquals(0.0, result.getTotalAmount());
        assertEquals(0, result.getItems().size());

        verify(cartRepository).findById(1L);
    }


    // Test 3: Get Cart By Id - Not Found
    @Test
    void getCartById_shouldThrowExceptionWhenCartNotFound() {

        when(cartRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> cartService.getCartById(999L)
        );

        verify(cartRepository).findById(999L);
    }


    // Test 4: Get Cart By User - Success
    @Test
    void getCartByUser_shouldReturnCart() {

        User user = new User();
        user.setId(1L);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setCartItems(new ArrayList<>());

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        CartResponseDTO result =
                cartService.getCartByUser(1L);

        assertEquals(1L, result.getCartId());
        assertEquals(1L, result.getUserId());
        assertEquals(0.0, result.getTotalAmount());

        verify(userRepository).findById(1L);
        verify(cartRepository).findByUser(user);
    }


    // Test 5: Get Cart By User - User Not Found
    @Test
    void getCartByUser_shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> cartService.getCartByUser(999L)
        );

        verify(userRepository).findById(999L);
    }


    // Test 6: Delete Cart - Success
    @Test
    void deleteCart_shouldDeleteCart() {

        when(cartRepository.existsById(1L))
                .thenReturn(true);

        cartService.deleteCart(1L);

        verify(cartRepository).existsById(1L);
        verify(cartRepository).deleteById(1L);
    }


    // Test 7: Delete Cart - Not Found
    @Test
    void deleteCart_shouldThrowExceptionWhenCartNotFound() {

        when(cartRepository.existsById(999L))
                .thenReturn(false);

        assertThrows(
                RuntimeException.class,
                () -> cartService.deleteCart(999L)
        );

        verify(cartRepository).existsById(999L);
    }
}