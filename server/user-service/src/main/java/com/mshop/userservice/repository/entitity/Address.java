package com.mshop.userservice.repository.entitity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "addresses")
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @Column(name = "address_id")
    private String addressId;

    @Column(name = "province_id")
    private int provinceID;

    @Column(name = "district_id")
    private int districtID;

    @Column(name = "ward_code")
    private String wardCode;

    @Column(name = "province_name")
    private String provinceName;

    @Column(name = "district_name")
    private String districtName;

    @Column(name = "ward_name")
    private String wardName;

    @Column(name = "address")
    private String address;

    @Transient
    private String fullAddress;

    public Address(String addressId, int provinceID, int districtID, String wardCode, String provinceName, String districtName, String wardName, String address) {
        this.addressId = addressId;
        this.provinceID = provinceID;
        this.districtID = districtID;
        this.wardCode = wardCode;
        this.provinceName = provinceName;
        this.districtName = districtName;
        this.wardName = wardName;
        this.address = address;
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    private void setFullAddress() {
        StringBuilder builder = new StringBuilder();
        if (!address.isBlank()) builder.append(address);
        if (!wardName.isBlank()) {
            if (builder.length() > 0) builder.append(", ");
            builder.append(wardName);
        }
        if (!districtName.isBlank()) {
            if (builder.length() > 0) builder.append(", ");
            builder.append(districtName);
        }
        if (!provinceName.isBlank()) {
            if (builder.length() > 0) builder.append(", ");
            builder.append(provinceName);
        }
        if (builder.length() > 0) {
            fullAddress = builder.toString();
        } else {
            fullAddress = "";
        }
    }
}
