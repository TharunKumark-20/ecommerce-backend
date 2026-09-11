package com.ecommerce.ecommercebackend.service;

import com.ecommerce.ecommercebackend.entity.RefreshToken;
import com.ecommerce.ecommercebackend.entity.User;
import com.ecommerce.ecommercebackend.repository.RefreshTokenRepository;
import com.ecommerce.ecommercebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class RefreshTokenService {


    private final RefreshTokenRepository refreshTokenRepository;

    private final UserRepository userRepository;


    // Refresh token validity duration
    private final long refreshTokenDuration = 7 * 24 * 60 * 60;



    public RefreshToken createRefreshToken(Long userId) {


        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );


        // Remove old refresh token if exists
        refreshTokenRepository.deleteByUserId(userId);



        RefreshToken refreshToken = new RefreshToken();


        refreshToken.setUser(user);


        refreshToken.setToken(
                UUID.randomUUID().toString()
        );


        refreshToken.setExpiryDate(
                Instant.now()
                        .plusSeconds(refreshTokenDuration)
        );


        return refreshTokenRepository.save(refreshToken);
    }



    public Optional<RefreshToken> findByToken(String token) {

        return refreshTokenRepository.findByToken(token);

    }



    public RefreshToken verifyExpiration(
            RefreshToken refreshToken) {


        if (refreshToken.getExpiryDate()
                .compareTo(Instant.now()) < 0) {


            refreshTokenRepository.delete(refreshToken);


            throw new RuntimeException(
                    "Refresh token expired"
            );
        }


        return refreshToken;

    }



    public void deleteByUserId(Long userId) {

        refreshTokenRepository.deleteByUserId(userId);

    }
}