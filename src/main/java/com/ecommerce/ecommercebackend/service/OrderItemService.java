package com.ecommerce.ecommercebackend.service;

import com.ecommerce.ecommercebackend.dto.OrderItemRequestDTO;
import com.ecommerce.ecommercebackend.entity.Order;
import com.ecommerce.ecommercebackend.entity.OrderItem;
import com.ecommerce.ecommercebackend.entity.Product;
import com.ecommerce.ecommercebackend.repository.OrderItemRepository;
import com.ecommerce.ecommercebackend.repository.OrderRepository;
import com.ecommerce.ecommercebackend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderItem saveOrderItem(OrderItemRequestDTO orderItemRequestDTO) {

        Order order = orderRepository.findById(orderItemRequestDTO.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Product product = productRepository.findById(orderItemRequestDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(orderItemRequestDTO.getQuantity());
        orderItem.setOrder(order);
        orderItem.setProduct(product);

        return orderItemRepository.save(orderItem);
    }

    public List<OrderItem> getAllOrderItems() {
        return orderItemRepository.findAll();
    }

    public Optional<OrderItem> getOrderItemById(Long id) {
        return orderItemRepository.findById(id);
    }

    public void deleteOrderItem(Long id) {
        if (!orderItemRepository.existsById(id)) {
            throw new RuntimeException("Order Item not found");
        }

        orderItemRepository.deleteById(id);
    }
}