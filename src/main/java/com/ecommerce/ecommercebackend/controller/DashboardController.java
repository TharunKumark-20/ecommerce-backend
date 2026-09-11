package com.ecommerce.ecommercebackend.controller;

import com.ecommerce.ecommercebackend.dto.DashboardResponseDTO;
import com.ecommerce.ecommercebackend.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponseDTO> getDashboard() {

        return ResponseEntity.ok(
                dashboardService.getDashboardData()
        );
    }
}