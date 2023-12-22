package com.mshop.orderservice.controller.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpsertOrderPromotionDto {
    private String promotionId;
    private String orderSupplierId;
    private Double totalPrice, totalPromotionPrice, finalPrice;
}
