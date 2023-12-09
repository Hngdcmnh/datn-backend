package com.sudo248.orderservice.controller.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierInfoDto implements Serializable {
    private String supplierId;
    private int ghnShopId;
    private String name;
    private String avatar;
    private String contactUrl;
    private AddressDto address;
    private float rate;
}


