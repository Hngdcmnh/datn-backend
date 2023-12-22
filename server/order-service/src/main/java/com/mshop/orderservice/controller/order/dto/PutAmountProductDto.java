package com.mshop.orderservice.controller.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PutAmountProductDto {
    private String productId;
    private int amount;
}
