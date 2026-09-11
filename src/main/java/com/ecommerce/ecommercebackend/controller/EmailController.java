package com.ecommerce.ecommercebackend.controller;

import com.ecommerce.ecommercebackend.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/test")
    public ResponseEntity<String> sendTestEmail() {

        emailService.sendEmail(
                "tharunkumark20122003@gmail.com",
                "Spring Boot Email Test",
                "Congratulations! 🎉 Your Spring Boot Email Service is working successfully."
        );

        return ResponseEntity.ok("Email sent successfully.");
    }
}