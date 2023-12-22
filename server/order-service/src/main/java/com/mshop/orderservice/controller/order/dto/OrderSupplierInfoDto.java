package com.mshop.orderservice.controller.order.dto;

import com.mshop.orderservice.repository.entity.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// for staff
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSupplierInfoDto {
    private String orderSupplierId;
    private String supplierId;
    private String supplierName;
    private String userFullName;
    private String userPhoneNumber;
    private String paymentType;
    private LocalDateTime paymentDateTime;
    private OrderStatus status;
    private String address;
    private LocalDateTime expectedReceiveDateTime;
    private double totalPrice;
    private LocalDateTime createdAt;
}
