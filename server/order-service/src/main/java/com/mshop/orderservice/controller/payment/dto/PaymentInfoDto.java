package com.mshop.orderservice.controller.payment.dto;

import com.mshop.orderservice.repository.entity.payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentInfoDto {
    private String paymentId;
    private Double amount;
    private String paymentType;
    private LocalDateTime paymentDateTime;
    private PaymentStatus status;
}
