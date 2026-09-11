package com.ecommerce.ecommercebackend.service;

import com.ecommerce.ecommercebackend.dto.AuthResponseDTO;
import com.ecommerce.ecommercebackend.dto.LoginRequestDTO;
import com.ecommerce.ecommercebackend.dto.RegisterRequestDTO;
import com.ecommerce.ecommercebackend.entity.Cart;
import com.ecommerce.ecommercebackend.entity.RefreshToken;
import com.ecommerce.ecommercebackend.entity.Role;
import com.ecommerce.ecommercebackend.entity.User;
import com.ecommerce.ecommercebackend.repository.CartRepository;
import com.ecommerce.ecommercebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger =
            LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;

    private final CartRepository cartRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final RefreshTokenService refreshTokenService;


    @Transactional
    public User register(RegisterRequestDTO request) {

        logger.info(
                "Registration attempt for email: {}",
                request.getEmail()
        );


        if (userRepository.existsByEmail(request.getEmail())) {

            logger.warn(
                    "Registration failed. Email already exists: {}",
                    request.getEmail()
            );

            throw new RuntimeException(
                    "Email already exists"
            );
        }


        User user = new User();


        user.setName(
                request.getName()
        );

        user.setEmail(
                request.getEmail()
        );

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        user.setRole(
                Role.USER
        );


        User savedUser =
                userRepository.save(user);


        // Create an empty cart for the new user
        Cart cart = new Cart();

        cart.setUser(savedUser);

        cartRepository.save(cart);


        logger.info(
                "User registered successfully with email: {}",
                savedUser.getEmail()
        );

        logger.info(
                "Cart created successfully for user id: {}",
                savedUser.getId()
        );


        return savedUser;
    }


    public AuthResponseDTO login(LoginRequestDTO request) {

        logger.info(
                "Login attempt for email: {}",
                request.getEmail()
        );


        authenticationManager.authenticate(

                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );


        User user =
                userRepository.findByEmail(
                                request.getEmail()
                        )
                        .orElseThrow(() -> {

                            logger.error(
                                    "User not found after authentication: {}",
                                    request.getEmail()
                            );

                            return new RuntimeException(
                                    "User not found"
                            );

                        });


        String accessToken =
                jwtService.generateToken(
                        user.getEmail(),
                        user.getRole().name()
                );


        logger.info(
                "Access token generated for email: {}",
                user.getEmail()
        );


        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(
                        user.getId()
                );


        logger.info(
                "Refresh token created for user: {}",
                user.getEmail()
        );


        return new AuthResponseDTO(
                accessToken,
                refreshToken.getToken(),
                user.getId()
        );
    }


    public AuthResponseDTO refreshToken(
            String requestRefreshToken) {

        logger.info(
                "Refreshing access token"
        );


        RefreshToken refreshToken =
                refreshTokenService.findByToken(
                                requestRefreshToken
                        )
                        .orElseThrow(() -> {

                            logger.error(
                                    "Refresh token not found"
                            );

                            return new RuntimeException(
                                    "Refresh token not found"
                            );

                        });


        refreshTokenService.verifyExpiration(
                refreshToken
        );


        User user =
                refreshToken.getUser();


        String newAccessToken =
                jwtService.generateToken(
                        user.getEmail(),
                        user.getRole().name()
                );


        logger.info(
                "New access token generated for user: {}",
                user.getEmail()
        );


        return new AuthResponseDTO(
                newAccessToken,
                refreshToken.getToken(),
                user.getId()
        );
    }


    public void logout(Long userId) {

        logger.info(
                "Logout request for user id: {}",
                userId
        );


        refreshTokenService.deleteByUserId(
                userId
        );


        logger.info(
                "Logout successful for user id: {}",
                userId
        );
    }

}