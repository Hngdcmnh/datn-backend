package com.mshop.orderservice.controller.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RevenueStatisticData {
    private Map<String, Double> data;
    private Double total;
}
