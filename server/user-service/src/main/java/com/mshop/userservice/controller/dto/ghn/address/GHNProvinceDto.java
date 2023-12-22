package com.mshop.userservice.controller.dto.ghn.address;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mshop.userservice.controller.dto.AddressSuggestionDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GHNProvinceDto {

    @JsonProperty("ProvinceID")
    public int provinceID;
    @JsonProperty("ProvinceName")
    public String provinceName;
    @JsonProperty("CountryID")
    public int countryID;
    @JsonProperty("Code")
    public String code;
    @JsonProperty("NameExtension")
    public ArrayList<String> nameExtension;
    @JsonProperty("IsEnable")
    public int isEnable;
    @JsonProperty("RegionID")
    public int regionID;
    @JsonProperty("RegionCPN")
    public int regionCPN;
    @JsonProperty("UpdatedBy")
    public int updatedBy;
    @JsonProperty("CreatedAt")
    public String createdAt;
    @JsonProperty("UpdatedAt")
    public String updatedAt;
    @JsonProperty("CanUpdateCOD")
    public boolean canUpdateCOD;
    @JsonProperty("Status")
    public int status;
    @JsonProperty("UpdatedIP")
    public String updatedIP;
    @JsonProperty("UpdatedEmployee")
    public int updatedEmployee;
    @JsonProperty("UpdatedSource")
    public String updatedSource;
    @JsonProperty("UpdatedDate")
    public String updatedDate;

    public AddressSuggestionDto toAddressSuggestionDto() {
        return new AddressSuggestionDto(
                provinceID,
                provinceName,
                code
        );
    }

}
