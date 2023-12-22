package com.mshop.orderservice.controller.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderUserInfoDto {
    private String orderId;
    private double finalPrice;
    private LocalDateTime createdAt;
    private List<OrderCartProductDto> cartProducts;
}
