package com.sudo248.orderservice.controller.order.dto;

import com.google.common.base.Supplier;
import com.sudo248.orderservice.controller.payment.dto.PaymentInfoDto;
import com.sudo248.orderservice.repository.entity.order.OrderStatus;
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
public class OrderSupplierUserInfoDto {
    private String orderId;
    private String cartId;
    private PaymentInfoDto payment;
    private PromotionDto promotion;
    private UserDto user;
    private OrderStatus status;
    private String address;
    private SupplierInfoDto supplier;
    private Double totalPrice, totalPromotionPrice, finalPrice, totalShipmentPrice;
    private LocalDateTime createdAt;
    private List<OrderCartProductDto> cartProducts;
}

