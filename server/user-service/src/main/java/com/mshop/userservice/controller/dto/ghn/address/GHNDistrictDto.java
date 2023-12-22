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
public class GHNDistrictDto {
    @JsonProperty("DistrictID")
    public int districtID;
    @JsonProperty("ProvinceID")
    public int provinceID;
    @JsonProperty("DistrictName")
    public String districtName;
    @JsonProperty("Code")
    public String code;
    @JsonProperty("Type")
    public int type;
    @JsonProperty("SupportType")
    public int supportType;
    @JsonProperty("NameExtension")
    public ArrayList<String> nameExtension;
    @JsonProperty("IsEnable")
    public int isEnable;
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
    @JsonProperty("PickType")
    public int pickType;
    @JsonProperty("DeliverType")
    public int deliverType;
    @JsonProperty("WhiteListClient")
    public Object whiteListClient;
    @JsonProperty("WhiteListDistrict")
    public Object whiteListDistrict;
    @JsonProperty("ReasonCode")
    public String reasonCode;
    @JsonProperty("ReasonMessage")
    public String reasonMessage;
    @JsonProperty("OnDates")
    public Object onDates;
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
                districtID,
                districtName,
                code
        );
    }
}
