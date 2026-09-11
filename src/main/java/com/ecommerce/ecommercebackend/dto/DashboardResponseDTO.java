package com.ecommerce.ecommercebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponseDTO {

    private Long totalUsers;

    private Long totalProducts;

    private Long totalOrders;

    private Double totalRevenue;

    private Long pendingOrders;

    private Long deliveredOrders;

    private Long cancelledOrders;

}