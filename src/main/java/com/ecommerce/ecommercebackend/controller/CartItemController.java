package com.ecommerce.ecommercebackend.controller;

import com.ecommerce.ecommercebackend.dto.CartItemRequestDTO;
import com.ecommerce.ecommercebackend.entity.CartItem;
import com.ecommerce.ecommercebackend.service.CartItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cart-items")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;


    // Add Product to Cart
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping
    public ResponseEntity<CartItem> createCartItem(
            @Valid @RequestBody CartItemRequestDTO cartItemRequestDTO) {

        CartItem savedCartItem =
                cartItemService.saveCartItem(cartItemRequestDTO);

        return new ResponseEntity<>(
                savedCartItem,
                HttpStatus.CREATED
        );
    }


    // Update Cart Item Quantity
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CartItem> updateCartItemQuantity(
            @PathVariable Long id,
            @RequestParam Integer quantity) {

        CartItem updatedCartItem =
                cartItemService.updateQuantity(
                        id,
                        quantity
                );

        return ResponseEntity.ok(updatedCartItem);
    }


    // Get All Cart Items
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<CartItem>> getAllCartItems() {

        return ResponseEntity.ok(
                cartItemService.getAllCartItems()
        );

    }


    // Get Cart Item By Id
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Optional<CartItem>> getCartItemById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                cartItemService.getCartItemById(id)
        );

    }


    // Get All Items of a Cart
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/cart/{cartId}")
    public ResponseEntity<List<CartItem>> getCartItemsByCart(
            @PathVariable Long cartId) {

        return ResponseEntity.ok(
                cartItemService.getCartItemsByCart(cartId)
        );

    }


    // Delete Cart Item
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCartItem(
            @PathVariable Long id) {

        cartItemService.deleteCartItem(id);

        return ResponseEntity.ok(
                "Cart Item deleted successfully"
        );

    }
}