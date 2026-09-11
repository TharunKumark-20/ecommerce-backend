package com.ecommerce.ecommercebackend.service;

import com.ecommerce.ecommercebackend.dto.CartItemRequestDTO;
import com.ecommerce.ecommercebackend.entity.Cart;
import com.ecommerce.ecommercebackend.entity.CartItem;
import com.ecommerce.ecommercebackend.entity.Product;
import com.ecommerce.ecommercebackend.repository.CartItemRepository;
import com.ecommerce.ecommercebackend.repository.CartRepository;
import com.ecommerce.ecommercebackend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;


    // Add Product to Cart
    public CartItem saveCartItem(
            CartItemRequestDTO cartItemRequestDTO) {

        Cart cart =
                cartRepository.findById(
                                cartItemRequestDTO.getCartId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException("Cart not found")
                        );


        Product product =
                productRepository.findById(
                                cartItemRequestDTO.getProductId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException("Product not found")
                        );


        Optional<CartItem> existingCartItem =
                cartItemRepository.findByCartAndProduct(
                        cart,
                        product
                );


        if (existingCartItem.isPresent()) {

            CartItem cartItem =
                    existingCartItem.get();


            int updatedQuantity =
                    cartItem.getQuantity()
                            + cartItemRequestDTO.getQuantity();


            if (updatedQuantity > product.getStock()) {

                throw new RuntimeException(
                        "Only " +
                                product.getStock() +
                                " items are available in stock."
                );
            }


            cartItem.setQuantity(updatedQuantity);


            return cartItemRepository.save(cartItem);
        }


        if (cartItemRequestDTO.getQuantity()
                > product.getStock()) {

            throw new RuntimeException(
                    "Only " +
                            product.getStock() +
                            " items are available in stock."
            );
        }


        CartItem cartItem =
                new CartItem();


        cartItem.setCart(cart);

        cartItem.setProduct(product);

        cartItem.setQuantity(
                cartItemRequestDTO.getQuantity()
        );


        return cartItemRepository.save(cartItem);
    }


    // Update Cart Item Quantity
    public CartItem updateQuantity(
            Long cartItemId,
            Integer quantity) {


        if (quantity == null || quantity < 1) {

            throw new RuntimeException(
                    "Quantity must be at least 1"
            );
        }


        CartItem cartItem =
                cartItemRepository.findById(cartItemId)

                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Cart Item not found"
                                )
                        );


        Product product =
                cartItem.getProduct();


        if (quantity > product.getStock()) {

            throw new RuntimeException(
                    "Only " +
                            product.getStock() +
                            " items are available in stock."
            );
        }


        cartItem.setQuantity(quantity);


        return cartItemRepository.save(cartItem);
    }


    // View All Cart Items
    public List<CartItem> getAllCartItems() {

        return cartItemRepository.findAll();

    }


    // Get Cart Item By Id
    public Optional<CartItem> getCartItemById(Long id) {

        return cartItemRepository.findById(id);

    }


    // View Items of a Particular Cart
    public List<CartItem> getCartItemsByCart(
            Long cartId) {


        Cart cart =
                cartRepository.findById(cartId)

                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Cart not found"
                                )
                        );


        return cartItemRepository.findByCart(cart);

    }


    // Delete Cart Item
    public void deleteCartItem(Long id) {

        if (!cartItemRepository.existsById(id)) {

            throw new RuntimeException(
                    "Cart Item not found"
            );
        }


        cartItemRepository.deleteById(id);

    }
}