package com.ecommerce.ecommercebackend.controller;

import com.ecommerce.ecommercebackend.dto.AuthResponseDTO;
import com.ecommerce.ecommercebackend.dto.LoginRequestDTO;
import com.ecommerce.ecommercebackend.dto.RefreshTokenRequestDTO;
import com.ecommerce.ecommercebackend.dto.RegisterRequestDTO;
import com.ecommerce.ecommercebackend.entity.User;
import com.ecommerce.ecommercebackend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<User> register(
            @Valid @RequestBody RegisterRequestDTO request) {

        User user = authService.register(request);

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request) {

        return ResponseEntity.ok(
                authService.login(request)
        );
    }


    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(
            @RequestBody RefreshTokenRequestDTO request) {

        return ResponseEntity.ok(
                authService.refreshToken(
                        request.getRefreshToken()
                )
        );
    }


    @PostMapping("/logout/{userId}")
    public ResponseEntity<String> logout(
            @PathVariable Long userId) {

        authService.logout(userId);

        return ResponseEntity.ok(
                "Logout successful"
        );
    }
}