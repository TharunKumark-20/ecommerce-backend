package com.ecommerce.ecommercebackend.service;

import com.ecommerce.ecommercebackend.dto.CartItemRequestDTO;
import com.ecommerce.ecommercebackend.entity.Cart;
import com.ecommerce.ecommercebackend.entity.CartItem;
import com.ecommerce.ecommercebackend.entity.Product;
import com.ecommerce.ecommercebackend.repository.CartItemRepository;
import com.ecommerce.ecommercebackend.repository.CartRepository;
import com.ecommerce.ecommercebackend.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartItemService cartItemService;


    // Test 1: Add New Product to Cart
    @Test
    void saveCartItem_shouldAddNewProductToCart() {

        CartItemRequestDTO request = new CartItemRequestDTO();

        request.setCartId(1L);
        request.setProductId(10L);
        request.setQuantity(2);


        Cart cart = new Cart();

        cart.setId(1L);


        Product product = new Product();

        product.setId(10L);
        product.setName("Laptop");
        product.setPrice(50000.0);
        product.setStock(10);


        when(cartRepository.findById(1L))
                .thenReturn(Optional.of(cart));

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        when(cartItemRepository.findByCartAndProduct(cart, product))
                .thenReturn(Optional.empty());


        CartItem savedCartItem = new CartItem();

        savedCartItem.setId(100L);
        savedCartItem.setCart(cart);
        savedCartItem.setProduct(product);
        savedCartItem.setQuantity(2);


        when(cartItemRepository.save(
                org.mockito.ArgumentMatchers.any(CartItem.class)
        )).thenReturn(savedCartItem);


        CartItem result =
                cartItemService.saveCartItem(request);


        assertEquals(100L, result.getId());
        assertEquals(2, result.getQuantity());
        assertEquals(product, result.getProduct());
        assertEquals(cart, result.getCart());


        verify(cartRepository).findById(1L);
        verify(productRepository).findById(10L);
        verify(cartItemRepository)
                .findByCartAndProduct(cart, product);
        verify(cartItemRepository)
                .save(org.mockito.ArgumentMatchers.any(CartItem.class));
    }


    // Test 2: Add Existing Product - Quantity Updated
    @Test
    void saveCartItem_shouldIncreaseExistingQuantity() {

        CartItemRequestDTO request = new CartItemRequestDTO();

        request.setCartId(1L);
        request.setProductId(10L);
        request.setQuantity(3);


        Cart cart = new Cart();

        cart.setId(1L);


        Product product = new Product();

        product.setId(10L);
        product.setName("Laptop");
        product.setPrice(50000.0);
        product.setStock(10);


        CartItem existingCartItem = new CartItem();

        existingCartItem.setId(100L);
        existingCartItem.setCart(cart);
        existingCartItem.setProduct(product);
        existingCartItem.setQuantity(2);


        when(cartRepository.findById(1L))
                .thenReturn(Optional.of(cart));

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        when(cartItemRepository.findByCartAndProduct(cart, product))
                .thenReturn(Optional.of(existingCartItem));

        when(cartItemRepository.save(existingCartItem))
                .thenReturn(existingCartItem);


        CartItem result =
                cartItemService.saveCartItem(request);


        assertEquals(5, result.getQuantity());


        verify(cartItemRepository)
                .save(existingCartItem);
    }


    // Test 3: Cart Not Found
    @Test
    void saveCartItem_shouldThrowExceptionWhenCartNotFound() {

        CartItemRequestDTO request = new CartItemRequestDTO();

        request.setCartId(999L);
        request.setProductId(10L);
        request.setQuantity(2);


        when(cartRepository.findById(999L))
                .thenReturn(Optional.empty());


        assertThrows(
                RuntimeException.class,
                () -> cartItemService.saveCartItem(request)
        );


        verify(cartRepository).findById(999L);
    }


    // Test 4: Product Not Found
    @Test
    void saveCartItem_shouldThrowExceptionWhenProductNotFound() {

        CartItemRequestDTO request = new CartItemRequestDTO();

        request.setCartId(1L);
        request.setProductId(999L);
        request.setQuantity(2);


        Cart cart = new Cart();

        cart.setId(1L);


        when(cartRepository.findById(1L))
                .thenReturn(Optional.of(cart));

        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());


        assertThrows(
                RuntimeException.class,
                () -> cartItemService.saveCartItem(request)
        );


        verify(cartRepository).findById(1L);
        verify(productRepository).findById(999L);
    }


    // Test 5: Quantity Greater Than Stock
    @Test
    void saveCartItem_shouldThrowExceptionWhenQuantityExceedsStock() {

        CartItemRequestDTO request = new CartItemRequestDTO();

        request.setCartId(1L);
        request.setProductId(10L);
        request.setQuantity(15);


        Cart cart = new Cart();

        cart.setId(1L);


        Product product = new Product();

        product.setId(10L);
        product.setName("Laptop");
        product.setStock(10);


        when(cartRepository.findById(1L))
                .thenReturn(Optional.of(cart));

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        when(cartItemRepository.findByCartAndProduct(cart, product))
                .thenReturn(Optional.empty());


        assertThrows(
                RuntimeException.class,
                () -> cartItemService.saveCartItem(request)
        );


        verify(cartRepository).findById(1L);
        verify(productRepository).findById(10L);
    }


    // Test 6: Get All Cart Items
    @Test
    void getAllCartItems_shouldReturnCartItems() {

        CartItem item1 = new CartItem();
        item1.setId(1L);

        CartItem item2 = new CartItem();
        item2.setId(2L);


        when(cartItemRepository.findAll())
                .thenReturn(List.of(item1, item2));


        List<CartItem> result =
                cartItemService.getAllCartItems();


        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());


        verify(cartItemRepository).findAll();
    }


    // Test 7: Get Cart Item By Id
    @Test
    void getCartItemById_shouldReturnCartItem() {

        CartItem item = new CartItem();

        item.setId(100L);


        when(cartItemRepository.findById(100L))
                .thenReturn(Optional.of(item));


        Optional<CartItem> result =
                cartItemService.getCartItemById(100L);


        assertEquals(100L, result.get().getId());


        verify(cartItemRepository).findById(100L);
    }


    // Test 8: Get Cart Items By Cart
    @Test
    void getCartItemsByCart_shouldReturnItems() {

        Cart cart = new Cart();

        cart.setId(1L);


        CartItem item1 = new CartItem();
        item1.setId(100L);

        CartItem item2 = new CartItem();
        item2.setId(101L);


        when(cartRepository.findById(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCart(cart))
                .thenReturn(List.of(item1, item2));


        List<CartItem> result =
                cartItemService.getCartItemsByCart(1L);


        assertEquals(2, result.size());


        verify(cartRepository).findById(1L);
        verify(cartItemRepository).findByCart(cart);
    }


    // Test 9: Delete Cart Item
    @Test
    void deleteCartItem_shouldDeleteCartItem() {

        when(cartItemRepository.existsById(100L))
                .thenReturn(true);


        cartItemService.deleteCartItem(100L);


        verify(cartItemRepository).existsById(100L);
        verify(cartItemRepository).deleteById(100L);
    }


    // Test 10: Delete Cart Item - Not Found
    @Test
    void deleteCartItem_shouldThrowExceptionWhenCartItemNotFound() {

        when(cartItemRepository.existsById(999L))
                .thenReturn(false);


        assertThrows(
                RuntimeException.class,
                () -> cartItemService.deleteCartItem(999L)
        );


        verify(cartItemRepository).existsById(999L);
    }
}