package com.ecommerce.ecommercebackend.service;

import com.ecommerce.ecommercebackend.entity.Cart;
import com.ecommerce.ecommercebackend.entity.CartItem;
import com.ecommerce.ecommercebackend.entity.Order;
import com.ecommerce.ecommercebackend.entity.OrderItem;
import com.ecommerce.ecommercebackend.entity.Product;
import com.ecommerce.ecommercebackend.repository.CartItemRepository;
import com.ecommerce.ecommercebackend.repository.CartRepository;
import com.ecommerce.ecommercebackend.repository.OrderItemRepository;
import com.ecommerce.ecommercebackend.repository.OrderRepository;
import com.ecommerce.ecommercebackend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class CheckoutService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public Order checkout(Long cartId) {

        // Find the cart
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        // Check if cart is empty
        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Create a new order
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderDate(LocalDateTime.now());

        order = orderRepository.save(order);

        // Move each CartItem to OrderItem
        for (CartItem cartItem : cart.getCartItems()) {

            Product product = cartItem.getProduct();

            // Final stock validation
            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException(
                        product.getName() + " is out of stock."
                );
            }

            // Create OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());

            orderItemRepository.save(orderItem);

            // Reduce product stock
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }

        // Remove all items from the cart
        cartItemRepository.deleteAll(cart.getCartItems());

        return order;
    }
}