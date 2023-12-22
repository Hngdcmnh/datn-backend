package com.mshop.orderservice.controller.order.dto.ghn;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mshop.orderservice.external.GHNService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalculateFeeRequest {
    @JsonProperty("service_type_id")
    private int serviceTypeId = GHNService.DEFAULT_SHIPMENT_TYPE.ordinal();

    @JsonProperty("insurance_value")
    private int insuranceValue = 0;

    @JsonProperty("coupon")
    private String coupon = null;

    @JsonProperty("from_district_id")
    private int fromDistrictId;

    @JsonProperty("from_ward_code")
    private String fromWardCode;

    @JsonProperty("to_district_id")
    private int toDistrictId;

    @JsonProperty("to_ward_code")
    private String toWardCode;

    @JsonProperty("weight")
    private int weight;

    @JsonProperty("length")
    private int length;

    @JsonProperty("width")
    private int width;

    @JsonProperty("height")
    private int height;

    @JsonProperty("cod_value")
    private int codValue = 0;

}
