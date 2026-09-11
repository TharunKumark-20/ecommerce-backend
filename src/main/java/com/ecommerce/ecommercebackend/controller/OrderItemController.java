package com.ecommerce.ecommercebackend.controller;

import com.ecommerce.ecommercebackend.dto.OrderItemRequestDTO;
import com.ecommerce.ecommercebackend.entity.OrderItem;
import com.ecommerce.ecommercebackend.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/order-items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    @PostMapping
    public ResponseEntity<OrderItem> createOrderItem(@RequestBody OrderItemRequestDTO orderItemRequestDTO) {

        OrderItem savedOrderItem = orderItemService.saveOrderItem(orderItemRequestDTO);

        return new ResponseEntity<>(savedOrderItem, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrderItem>> getAllOrderItems() {
        return ResponseEntity.ok(orderItemService.getAllOrderItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<OrderItem>> getOrderItemById(@PathVariable Long id) {
        return ResponseEntity.ok(orderItemService.getOrderItemById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrderItem(@PathVariable Long id) {

        orderItemService.deleteOrderItem(id);

        return ResponseEntity.ok("Order Item deleted successfully");
    }
}