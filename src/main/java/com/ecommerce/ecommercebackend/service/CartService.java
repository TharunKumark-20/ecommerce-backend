package com.ecommerce.ecommercebackend.service;

import com.ecommerce.ecommercebackend.dto.CartItemRequestDTO;
import com.ecommerce.ecommercebackend.dto.CartItemResponseDTO;
import com.ecommerce.ecommercebackend.dto.CartRequestDTO;
import com.ecommerce.ecommercebackend.dto.CartResponseDTO;
import com.ecommerce.ecommercebackend.entity.Cart;
import com.ecommerce.ecommercebackend.entity.CartItem;
import com.ecommerce.ecommercebackend.entity.Product;
import com.ecommerce.ecommercebackend.entity.User;
import com.ecommerce.ecommercebackend.exception.ResourceNotFoundException;
import com.ecommerce.ecommercebackend.repository.CartItemRepository;
import com.ecommerce.ecommercebackend.repository.CartRepository;
import com.ecommerce.ecommercebackend.repository.ProductRepository;
import com.ecommerce.ecommercebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;


    // Create Cart
    public Cart saveCart(CartRequestDTO cartRequestDTO) {

        User user =
                userRepository.findById(cartRequestDTO.getUserId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        if (cartRepository.findByUser(user).isPresent()) {

            throw new RuntimeException(
                    "Cart already exists for this user"
            );
        }

        Cart cart = new Cart();

        cart.setUser(user);

        return cartRepository.save(cart);
    }


    // Add Product To Cart
    public CartResponseDTO addToCart(
            Long cartId,
            CartItemRequestDTO cartItemRequestDTO) {

        Cart cart =
                cartRepository.findById(cartId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Cart not found"
                                )
                        );

        Product product =
                productRepository.findById(
                                cartItemRequestDTO.getProductId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found"
                                )
                        );

        Integer quantity =
                cartItemRequestDTO.getQuantity();

        if (quantity == null || quantity <= 0) {

            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }

        CartItem cartItem =
                cartItemRepository
                        .findByCartAndProduct(cart, product)
                        .orElse(null);

        if (cartItem != null) {

            cartItem.setQuantity(
                    cartItem.getQuantity() + quantity
            );

        } else {

            cartItem = new CartItem();

            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
        }

        cartItemRepository.save(cartItem);

        return convertToDTO(cart);
    }


    // Get All Carts
    public List<CartResponseDTO> getAllCarts() {

        return cartRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }


    // Get Cart By Id
    public CartResponseDTO getCartById(Long id) {

        Cart cart =
                cartRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Cart not found"
                                )
                        );

        return convertToDTO(cart);
    }


    // Get Cart By User
    @Transactional
    public CartResponseDTO getCartByUser(Long userId) {

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );


        Cart cart =
                cartRepository.findByUser(user)
                        .orElseGet(() -> {

                            Cart newCart = new Cart();

                            newCart.setUser(user);

                            return cartRepository.save(newCart);

                        });


        return convertToDTO(cart);
    }


    // Delete Cart
    public void deleteCart(Long id) {

        if (!cartRepository.existsById(id)) {

            throw new ResourceNotFoundException(
                    "Cart not found"
            );
        }

        cartRepository.deleteById(id);
    }


    // Entity -> DTO Conversion
    private CartResponseDTO convertToDTO(Cart cart) {

        List<CartItemResponseDTO> items =
                cart.getCartItems()
                        .stream()
                        .map(this::convertCartItemToDTO)
                        .toList();

        Double totalAmount =
                items.stream()
                        .mapToDouble(
                                CartItemResponseDTO::getSubTotal
                        )
                        .sum();

        Long userId = null;

        if (cart.getUser() != null) {

            userId = cart.getUser().getId();
        }

        return new CartResponseDTO(
                cart.getId(),
                userId,
                items,
                totalAmount
        );
    }


    private CartItemResponseDTO convertCartItemToDTO(
            CartItem cartItem) {

        Double price =
                cartItem.getProduct().getPrice();

        Double subTotal =
                price * cartItem.getQuantity();

        return new CartItemResponseDTO(
                cartItem.getId(),
                cartItem.getProduct().getId(),
                cartItem.getProduct().getName(),
                price,
                cartItem.getQuantity(),
                subTotal
        );
    }
}