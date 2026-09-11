package com.ecommerce.ecommercebackend.service;

import com.ecommerce.ecommercebackend.dto.OrderRequestDTO;
import com.ecommerce.ecommercebackend.dto.OrderResponseDTO;
import com.ecommerce.ecommercebackend.dto.OrderStatusRequestDTO;
import com.ecommerce.ecommercebackend.entity.Cart;
import com.ecommerce.ecommercebackend.entity.CartItem;
import com.ecommerce.ecommercebackend.entity.Order;
import com.ecommerce.ecommercebackend.entity.OrderItem;
import com.ecommerce.ecommercebackend.entity.OrderStatus;
import com.ecommerce.ecommercebackend.entity.Product;
import com.ecommerce.ecommercebackend.entity.User;
import com.ecommerce.ecommercebackend.repository.CartItemRepository;
import com.ecommerce.ecommercebackend.repository.CartRepository;
import com.ecommerce.ecommercebackend.repository.OrderRepository;
import com.ecommerce.ecommercebackend.repository.ProductRepository;
import com.ecommerce.ecommercebackend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
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
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private OrderService orderService;


    // Test 1: Place Order Successfully
    @Test
    void saveOrder_shouldCreateOrderSuccessfully() {

        OrderRequestDTO request = new OrderRequestDTO();
        request.setUserId(1L);

        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);

        Product product = new Product();
        product.setId(10L);
        product.setName("Laptop");
        product.setPrice(50000.0);
        product.setStock(10);

        CartItem cartItem = new CartItem();
        cartItem.setId(100L);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        Order savedOrder = new Order();
        savedOrder.setId(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCart(cart))
                .thenReturn(List.of(cartItem));

        when(orderRepository.save(
                ArgumentMatchers.any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setId(1L);
                    return order;
                });

        Order result = orderService.saveOrder(request);

        assertEquals(1L, result.getId());
        assertEquals(user, result.getUser());
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertEquals(100000.0, result.getTotalAmount());
        assertEquals(1, result.getOrderItems().size());

        assertEquals(
                8,
                product.getStock()
        );

        verify(productRepository).save(product);
        verify(orderRepository).save(ArgumentMatchers.any(Order.class));
        verify(cartItemRepository).deleteAll(List.of(cartItem));
        verify(emailService).sendEmail(
                user.getEmail(),
                "Order Confirmation",
                "Hello " + user.getName()
                        + ",\n\nYour order with ID "
                        + result.getId()
                        + " has been placed successfully."
                        + "\n\nTotal Amount: "
                        + result.getTotalAmount()
        );
    }


    // Test 2: User Not Found
    @Test
    void saveOrder_shouldThrowExceptionWhenUserNotFound() {

        OrderRequestDTO request = new OrderRequestDTO();
        request.setUserId(999L);

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> orderService.saveOrder(request)
        );

        verify(userRepository).findById(999L);
    }


    // Test 3: Cart Not Found
    @Test
    void saveOrder_shouldThrowExceptionWhenCartNotFound() {

        OrderRequestDTO request = new OrderRequestDTO();
        request.setUserId(1L);

        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> orderService.saveOrder(request)
        );

        verify(userRepository).findById(1L);
        verify(cartRepository).findByUser(user);
    }


    // Test 4: Empty Cart
    @Test
    void saveOrder_shouldThrowExceptionWhenCartIsEmpty() {

        OrderRequestDTO request = new OrderRequestDTO();
        request.setUserId(1L);

        User user = new User();
        user.setId(1L);

        Cart cart = new Cart();
        cart.setId(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCart(cart))
                .thenReturn(new ArrayList<>());

        assertThrows(
                RuntimeException.class,
                () -> orderService.saveOrder(request)
        );

        verify(cartItemRepository).findByCart(cart);
    }


    // Test 5: Insufficient Stock
    @Test
    void saveOrder_shouldThrowExceptionWhenStockIsInsufficient() {

        OrderRequestDTO request = new OrderRequestDTO();
        request.setUserId(1L);

        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);

        Product product = new Product();
        product.setId(10L);
        product.setName("Laptop");
        product.setPrice(50000.0);
        product.setStock(1);

        CartItem cartItem = new CartItem();
        cartItem.setId(100L);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCart(cart))
                .thenReturn(List.of(cartItem));

        assertThrows(
                RuntimeException.class,
                () -> orderService.saveOrder(request)
        );
    }


    // Test 6: Get All Orders
    @Test
    void getAllOrders_shouldReturnOrders() {

        User user = new User();
        user.setId(1L);

        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setTotalAmount(50000.0);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderItems(new ArrayList<>());

        when(orderRepository.findAll())
                .thenReturn(List.of(order));

        List<OrderResponseDTO> result =
                orderService.getAllOrders();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getOrderId());
        assertEquals(1L, result.get(0).getUserId());
        assertEquals(50000.0, result.get(0).getTotalAmount());
        assertEquals(OrderStatus.PENDING, result.get(0).getStatus());

        verify(orderRepository).findAll();
    }


    // Test 7: Get Order By Id
    @Test
    void getOrderById_shouldReturnOrder() {

        User user = new User();
        user.setId(1L);

        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setTotalAmount(50000.0);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderItems(new ArrayList<>());

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        OrderResponseDTO result =
                orderService.getOrderById(1L);

        assertEquals(1L, result.getOrderId());
        assertEquals(1L, result.getUserId());
        assertEquals(50000.0, result.getTotalAmount());
        assertEquals(OrderStatus.PENDING, result.getStatus());

        verify(orderRepository).findById(1L);
    }


    // Test 8: Get Order By Id - Not Found
    @Test
    void getOrderById_shouldThrowExceptionWhenOrderNotFound() {

        when(orderRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> orderService.getOrderById(999L)
        );

        verify(orderRepository).findById(999L);
    }


    // Test 9: Get Orders By User
    @Test
    void getOrdersByUser_shouldReturnOrders() {

        User user = new User();
        user.setId(1L);

        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setTotalAmount(50000.0);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderItems(new ArrayList<>());

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(orderRepository.findByUser(user))
                .thenReturn(List.of(order));

        List<OrderResponseDTO> result =
                orderService.getOrdersByUser(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getOrderId());
        assertEquals(1L, result.get(0).getUserId());

        verify(userRepository).findById(1L);
        verify(orderRepository).findByUser(user);
    }


    // Test 10: Cancel Order
    @Test
    void cancelOrder_shouldCancelOrderAndRestoreStock() {

        Product product = new Product();
        product.setId(10L);
        product.setName("Laptop");
        product.setPrice(50000.0);
        product.setStock(8);

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        orderItem.setPrice(50000.0);

        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderItems(
                new ArrayList<>(List.of(orderItem))
        );

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(order))
                .thenReturn(order);

        Order result =
                orderService.cancelOrder(1L);

        assertEquals(OrderStatus.CANCELLED, result.getStatus());
        assertEquals(10, product.getStock());

        verify(productRepository).save(product);
        verify(orderRepository).save(order);
    }


    // Test 11: Cannot Cancel Delivered Order
    @Test
    void cancelOrder_shouldThrowExceptionWhenOrderIsDelivered() {

        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.DELIVERED);
        order.setOrderItems(new ArrayList<>());

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        assertThrows(
                RuntimeException.class,
                () -> orderService.cancelOrder(1L)
        );
    }


    // Test 12: Cannot Cancel Already Cancelled Order
    @Test
    void cancelOrder_shouldThrowExceptionWhenOrderAlreadyCancelled() {

        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.CANCELLED);
        order.setOrderItems(new ArrayList<>());

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        assertThrows(
                RuntimeException.class,
                () -> orderService.cancelOrder(1L)
        );
    }


    // Test 13: PENDING → CONFIRMED
    @Test
    void updateOrderStatus_shouldConfirmPendingOrder() {

        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);

        OrderStatusRequestDTO request = new OrderStatusRequestDTO();
        request.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(order))
                .thenReturn(order);

        Order result =
                orderService.updateOrderStatus(1L, request);

        assertEquals(OrderStatus.CONFIRMED, result.getStatus());

        verify(orderRepository).save(order);
    }


    // Test 14: CONFIRMED → SHIPPED
    @Test
    void updateOrderStatus_shouldShipConfirmedOrder() {

        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.CONFIRMED);

        OrderStatusRequestDTO request = new OrderStatusRequestDTO();
        request.setStatus(OrderStatus.SHIPPED);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(order))
                .thenReturn(order);

        Order result =
                orderService.updateOrderStatus(1L, request);

        assertEquals(OrderStatus.SHIPPED, result.getStatus());

        verify(orderRepository).save(order);
    }


    // Test 15: SHIPPED → DELIVERED
    @Test
    void updateOrderStatus_shouldDeliverShippedOrder() {

        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.SHIPPED);

        OrderStatusRequestDTO request = new OrderStatusRequestDTO();
        request.setStatus(OrderStatus.DELIVERED);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(order))
                .thenReturn(order);

        Order result =
                orderService.updateOrderStatus(1L, request);

        assertEquals(OrderStatus.DELIVERED, result.getStatus());

        verify(orderRepository).save(order);
    }


    // Test 16: Invalid Status Transition
    @Test
    void updateOrderStatus_shouldThrowExceptionForInvalidTransition() {

        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);

        OrderStatusRequestDTO request = new OrderStatusRequestDTO();
        request.setStatus(OrderStatus.SHIPPED);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        assertThrows(
                RuntimeException.class,
                () -> orderService.updateOrderStatus(1L, request)
        );
    }


    // Test 17: Cancelled Order Cannot Be Updated
    @Test
    void updateOrderStatus_shouldThrowExceptionForCancelledOrder() {

        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.CANCELLED);

        OrderStatusRequestDTO request = new OrderStatusRequestDTO();
        request.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        assertThrows(
                RuntimeException.class,
                () -> orderService.updateOrderStatus(1L, request)
        );
    }


    // Test 18: Delivered Order Cannot Be Updated
    @Test
    void updateOrderStatus_shouldThrowExceptionForDeliveredOrder() {

        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.DELIVERED);

        OrderStatusRequestDTO request = new OrderStatusRequestDTO();
        request.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        assertThrows(
                RuntimeException.class,
                () -> orderService.updateOrderStatus(1L, request)
        );
    }


    // Test 19: Delete Order
    @Test
    void deleteOrder_shouldDeleteOrder() {

        when(orderRepository.existsById(1L))
                .thenReturn(true);

        orderService.deleteOrder(1L);

        verify(orderRepository).existsById(1L);
        verify(orderRepository).deleteById(1L);
    }


    // Test 20: Delete Order - Not Found
    @Test
    void deleteOrder_shouldThrowExceptionWhenOrderNotFound() {

        when(orderRepository.existsById(999L))
                .thenReturn(false);

        assertThrows(
                RuntimeException.class,
                () -> orderService.deleteOrder(999L)
        );

        verify(orderRepository).existsById(999L);
    }
}