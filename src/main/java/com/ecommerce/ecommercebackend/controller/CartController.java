package com.ecommerce.ecommercebackend.controller;

import com.ecommerce.ecommercebackend.dto.CartItemRequestDTO;
import com.ecommerce.ecommercebackend.dto.CartRequestDTO;
import com.ecommerce.ecommercebackend.dto.CartResponseDTO;
import com.ecommerce.ecommercebackend.entity.Cart;
import com.ecommerce.ecommercebackend.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;


    // Create Cart
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping
    public ResponseEntity<Cart> createCart(
            @RequestBody CartRequestDTO cartRequestDTO
    ) {

        Cart savedCart =
                cartService.saveCart(cartRequestDTO);

        return new ResponseEntity<>(
                savedCart,
                HttpStatus.CREATED
        );
    }


    // Add Product To Cart
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartResponseDTO> addToCart(
            @PathVariable Long cartId,
            @RequestBody CartItemRequestDTO cartItemRequestDTO
    ) {

        CartResponseDTO updatedCart =
                cartService.addToCart(
                        cartId,
                        cartItemRequestDTO
                );

        return ResponseEntity.ok(updatedCart);
    }


    // Get All Carts
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<CartResponseDTO>> getAllCarts() {

        return ResponseEntity.ok(
                cartService.getAllCarts()
        );
    }


    // Get Cart By Id
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CartResponseDTO> getCartById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                cartService.getCartById(id)
        );
    }


    // Get Cart By User
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<CartResponseDTO> getCartByUser(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                cartService.getCartByUser(userId)
        );
    }


    // Delete Cart
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCart(
            @PathVariable Long id
    ) {

        cartService.deleteCart(id);

        return ResponseEntity.ok(
                "Cart deleted successfully"
        );
    }
}