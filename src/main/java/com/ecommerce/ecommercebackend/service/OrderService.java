package com.ecommerce.ecommercebackend.service;

import com.ecommerce.ecommercebackend.dto.OrderItemResponseDTO;
import com.ecommerce.ecommercebackend.dto.OrderRequestDTO;
import com.ecommerce.ecommercebackend.dto.OrderResponseDTO;
import com.ecommerce.ecommercebackend.dto.OrderStatusRequestDTO;
import com.ecommerce.ecommercebackend.entity.*;
import com.ecommerce.ecommercebackend.exception.ResourceNotFoundException;
import com.ecommerce.ecommercebackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {


    private static final Logger logger =
            LoggerFactory.getLogger(OrderService.class);


    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    private final ProductRepository productRepository;

    private final EmailService emailService;




    // Place Order
    @Transactional
    public Order saveOrder(OrderRequestDTO orderRequestDTO) {


        logger.info("Creating order for user id: {}",
                orderRequestDTO.getUserId());


        User user =
                userRepository.findById(orderRequestDTO.getUserId())

                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );



        Cart cart = getUserCart(user);



        List<CartItem> cartItems =
                getCartItems(cart);



        Order order = new Order();


        order.setUser(user);

        order.setOrderDate(LocalDateTime.now());

        order.setStatus(OrderStatus.PENDING);



        double totalAmount = 0.0;



        for (CartItem cartItem : cartItems) {


            Product product =
                    cartItem.getProduct();



            if(product.getStock() < cartItem.getQuantity()) {


                throw new RuntimeException(
                        product.getName()
                                + " is out of stock."
                );

            }



            OrderItem orderItem =
                    new OrderItem();


            orderItem.setOrder(order);

            orderItem.setProduct(product);

            orderItem.setQuantity(
                    cartItem.getQuantity()
            );

            orderItem.setPrice(
                    product.getPrice()
            );



            order.getOrderItems()
                    .add(orderItem);



            totalAmount +=
                    product.getPrice()
                            *
                            cartItem.getQuantity();



            product.setStock(
                    product.getStock()
                            -
                            cartItem.getQuantity()
            );



            productRepository.save(product);

        }



        order.setTotalAmount(totalAmount);



        Order savedOrder =
                orderRepository.save(order);



        cartItemRepository.deleteAll(cartItems);



        emailService.sendEmail(

                user.getEmail(),

                "Order Confirmation",

                "Hello "
                        + user.getName()
                        + ",\n\nYour order with ID "
                        + savedOrder.getId()
                        + " has been placed successfully."
                        + "\n\nTotal Amount: "
                        + savedOrder.getTotalAmount()

        );



        return savedOrder;

    }
    // Get All Orders
    public List<OrderResponseDTO> getAllOrders() {

        logger.info("Fetching all orders");

        return orderRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }





    // Get Order By Id
    public OrderResponseDTO getOrderById(Long id) {

        logger.info("Fetching order with id: {}", id);


        Order order =
                orderRepository.findById(id)

                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order not found"
                                )
                        );


        return convertToDTO(order);
    }





    // Get Orders By User
    public List<OrderResponseDTO> getOrdersByUser(Long userId) {


        User user =
                userRepository.findById(userId)

                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );


        return orderRepository.findByUser(user)

                .stream()

                .map(this::convertToDTO)

                .toList();
    }





    // Cancel Order
    @Transactional
    public Order cancelOrder(Long orderId) {


        Order order =
                orderRepository.findById(orderId)

                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order not found"
                                )
                        );



        if(order.getStatus() == OrderStatus.DELIVERED) {

            throw new RuntimeException(
                    "Delivered orders cannot be cancelled."
            );
        }



        if(order.getStatus() == OrderStatus.CANCELLED) {

            throw new RuntimeException(
                    "Order is already cancelled."
            );
        }



        for(OrderItem orderItem : order.getOrderItems()) {


            Product product =
                    orderItem.getProduct();



            product.setStock(
                    product.getStock()
                            +
                            orderItem.getQuantity()
            );


            productRepository.save(product);

        }



        order.setStatus(OrderStatus.CANCELLED);


        return orderRepository.save(order);
    }





    // Update Order Status
    @Transactional
    public Order updateOrderStatus(
            Long orderId,
            OrderStatusRequestDTO requestDTO) {


        Order order =
                orderRepository.findById(orderId)

                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order not found"
                                )
                        );



        OrderStatus currentStatus =
                order.getStatus();


        OrderStatus newStatus =
                requestDTO.getStatus();




        if(currentStatus == OrderStatus.CANCELLED) {

            throw new RuntimeException(
                    "Cancelled order cannot be updated."
            );

        }



        if(currentStatus == OrderStatus.DELIVERED) {

            throw new RuntimeException(
                    "Delivered order cannot be updated."
            );

        }




        if(currentStatus == OrderStatus.PENDING
                &&
                newStatus == OrderStatus.CONFIRMED) {


            order.setStatus(newStatus);


        }
        else if(currentStatus == OrderStatus.CONFIRMED
                &&
                newStatus == OrderStatus.SHIPPED) {


            order.setStatus(newStatus);


        }
        else if(currentStatus == OrderStatus.SHIPPED
                &&
                newStatus == OrderStatus.DELIVERED) {


            order.setStatus(newStatus);


        }
        else {

            throw new RuntimeException(
                    "Invalid order status transition."
            );

        }


        return orderRepository.save(order);

    }





    // Delete Order
    public void deleteOrder(Long id) {


        if(!orderRepository.existsById(id)) {


            throw new ResourceNotFoundException(
                    "Order not found"
            );

        }


        orderRepository.deleteById(id);

    }





    // Convert Order Entity -> DTO
    private OrderResponseDTO convertToDTO(Order order) {


        List<OrderItemResponseDTO> items =

                order.getOrderItems()

                        .stream()

                        .map(this::convertOrderItemToDTO)

                        .toList();



        Long userId = null;


        if(order.getUser() != null) {

            userId = order.getUser().getId();

        }



        return new OrderResponseDTO(

                order.getId(),

                userId,

                order.getOrderDate(),

                order.getTotalAmount(),

                order.getStatus(),

                items

        );

    }





    // Convert OrderItem Entity -> DTO
    private OrderItemResponseDTO convertOrderItemToDTO(
            OrderItem orderItem) {


        Product product =
                orderItem.getProduct();



        Double subTotal =
                orderItem.getPrice()
                        *
                        orderItem.getQuantity();



        return new OrderItemResponseDTO(

                product.getId(),

                product.getName(),

                orderItem.getQuantity(),

                orderItem.getPrice(),

                subTotal

        );

    }





    // Helper Method - Get User Cart
    private Cart getUserCart(User user) {


        return cartRepository.findByUser(user)

                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cart not found"
                        )
                );

    }





    // Helper Method - Get Cart Items
    private List<CartItem> getCartItems(Cart cart) {


        List<CartItem> cartItems =
                cartItemRepository.findByCart(cart);



        if(cartItems.isEmpty()) {

            throw new RuntimeException(
                    "Cart is empty"
            );

        }


        return cartItems;

    }

}
