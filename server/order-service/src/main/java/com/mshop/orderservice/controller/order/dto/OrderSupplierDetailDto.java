package com.mshop.orderservice.controller.order.dto;

import com.mshop.orderservice.repository.entity.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSupplierDetailDto {
    private String orderSupplierId;
    private SupplierInfoDto supplier;
    private UserDto user;
    private String paymentType;
    private OrderStatus status;
    private String address;
    private LocalDateTime expectedReceiveDateTime;
    private double totalPrice;
    private LocalDateTime createdAt;
    private List<OrderCartProductDto> orderCartProducts;
}
