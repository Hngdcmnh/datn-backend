package com.mshop.orderservice.controller.payment.dto;

import com.mshop.orderservice.repository.entity.payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private String paymentId;
    private String orderId;
    private String orderType;
    private String bankCode;
    private Double amount;
    private String paymentType;
    private String ipAddress;
    private String timeZoneId;
    private String paymentUrl;
    private PaymentStatus paymentStatus;
}
