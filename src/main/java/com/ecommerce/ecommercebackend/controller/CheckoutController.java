package com.ecommerce.ecommercebackend.controller;

import com.ecommerce.ecommercebackend.entity.Order;
import com.ecommerce.ecommercebackend.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping("/{cartId}")
    public ResponseEntity<Order> checkout(@PathVariable Long cartId) {

        Order order = checkoutService.checkout(cartId);

        return ResponseEntity.ok(order);
    }
}