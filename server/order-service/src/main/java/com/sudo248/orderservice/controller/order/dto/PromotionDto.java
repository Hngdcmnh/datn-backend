package com.sudo248.orderservice.controller.order.dto;

import com.sudo248.orderservice.repository.entity.order.Promotion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionDto {
    private String promotionId = "";
    private String name = "";
    private Double value = 0.0;
}
