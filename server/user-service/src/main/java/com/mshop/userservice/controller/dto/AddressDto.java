package com.mshop.userservice.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    private String addressId;

    private int provinceID;

    private int districtID;

    private String wardCode;

    private String provinceName;

    private String districtName;

    private String wardName;

    private String address;

    private String fullAddress;
}
