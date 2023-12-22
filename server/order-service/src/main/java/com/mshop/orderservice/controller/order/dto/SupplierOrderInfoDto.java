package com.mshop.orderservice.controller.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupplierOrderInfoDto {
    private String supplierId;
    private String name;
    private String avatar;
    private String brand;
    private String contactUrl;
}
