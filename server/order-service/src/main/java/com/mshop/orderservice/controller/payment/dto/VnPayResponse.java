package com.mshop.orderservice.controller.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VnPayResponse {
    private String RspCode;
    private String Message;
}
