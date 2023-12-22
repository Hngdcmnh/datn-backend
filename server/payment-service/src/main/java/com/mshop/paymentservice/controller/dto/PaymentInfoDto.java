package com.mshop.paymentservice.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInfoDto {
    private String paymentId;
    private Double amount;
    private String paymentType;
}
