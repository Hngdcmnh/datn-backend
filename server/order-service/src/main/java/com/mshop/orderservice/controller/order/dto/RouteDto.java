package com.mshop.orderservice.controller.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteDto {
    private double weight = 0.0;
    private ValueDto duration = new ValueDto();
    private ValueDto distance = new ValueDto();
}
