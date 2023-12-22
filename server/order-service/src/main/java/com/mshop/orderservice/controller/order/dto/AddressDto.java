package com.mshop.orderservice.controller.order.dto;

import com.mshop.orderservice.repository.entity.order.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDto {
    private String addressId;

    private int provinceID;

    private int districtID;

    private String wardCode;

    private String provinceName;

    private String districtName;

    private String wardName;

    private String address;

    private Location location;

    private String fullAddress;

    public AddressDto(String s, int i, int i1, int i2, String ninhBình, String thànhPhốNinhBình, String phườngĐôngThành, Location location, String s1) {
    }
}
