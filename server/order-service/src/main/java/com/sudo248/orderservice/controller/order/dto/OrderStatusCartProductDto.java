package com.sudo248.orderservice.controller.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusCartProductDto {
    private String orderId;
    private String cartProductId;
    private LocalDateTime createAd;
    private String cartId;
    private OrderProductInfoDto product;
    private int quantity;
    private double totalPrice;
}
