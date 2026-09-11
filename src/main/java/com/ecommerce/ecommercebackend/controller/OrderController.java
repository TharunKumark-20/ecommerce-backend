package com.ecommerce.ecommercebackend.controller;

import com.ecommerce.ecommercebackend.dto.OrderRequestDTO;
import com.ecommerce.ecommercebackend.dto.OrderResponseDTO;
import com.ecommerce.ecommercebackend.dto.OrderStatusRequestDTO;
import com.ecommerce.ecommercebackend.entity.Order;
import com.ecommerce.ecommercebackend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {


    private final OrderService orderService;



    // Create Order
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping
    public ResponseEntity<Order> createOrder(

            @Valid @RequestBody OrderRequestDTO orderRequestDTO

    ) {


        Order savedOrder =
                orderService.saveOrder(orderRequestDTO);


        return new ResponseEntity<>(
                savedOrder,
                HttpStatus.CREATED
        );
    }





    // Get All Orders
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {


        return ResponseEntity.ok(
                orderService.getAllOrders()
        );

    }





    // Get Order By Id
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(

            @PathVariable Long id

    ) {


        return ResponseEntity.ok(
                orderService.getOrderById(id)
        );

    }





    // Get Orders By User
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByUser(

            @PathVariable Long userId

    ) {


        return ResponseEntity.ok(
                orderService.getOrdersByUser(userId)
        );

    }





    // Cancel Order
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<Order> cancelOrder(

            @PathVariable Long orderId

    ) {


        return ResponseEntity.ok(
                orderService.cancelOrder(orderId)
        );

    }





    // Update Order Status
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/status/{orderId}")
    public ResponseEntity<Order> updateOrderStatus(

            @PathVariable Long orderId,

            @Valid @RequestBody OrderStatusRequestDTO requestDTO

    ) {


        return ResponseEntity.ok(
                orderService.updateOrderStatus(
                        orderId,
                        requestDTO
                )
        );

    }





    // Delete Order
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(

            @PathVariable Long id

    ) {


        orderService.deleteOrder(id);


        return ResponseEntity.ok(
                "Order deleted successfully"
        );

    }
}