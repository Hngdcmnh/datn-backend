package com.mshop.orderservice.controller.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfoDto {
    private String productId;
    private String supplierId;
    private String sku;
    private String name;
    private float price;
    private float listedPrice;
    private int amount;
    private int discount;
    private LocalDateTime startDateDiscount;
    private LocalDateTime endDateDiscount;
    private String brand;
    private float rate;
    private boolean saleable;
    private List<String> images;
}