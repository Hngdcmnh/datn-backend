package com.mshop.notification.controller.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierInfoDto {
    private String supplierId;
    private String userId;
    private String name;
    private String brand;
    private String locationName;
    private String contactUrl;
    private Float rate;
}
