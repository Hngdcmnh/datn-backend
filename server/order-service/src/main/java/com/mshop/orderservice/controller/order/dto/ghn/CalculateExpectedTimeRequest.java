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
public class CalculateExpectedTimeRequest {

    @JsonProperty("service_id")
    private int serviceId = GHNService.DEFAULT_SHIPMENT_TYPE.ordinal();

    @JsonProperty("from_district_id")
    private int fromDistrictId;

    @JsonProperty("from_ward_code")
    private String fromWardCode;

    @JsonProperty("to_district_id")
    private int toDistrictId;

    @JsonProperty("to_ward_code")
    private String toWardCode;

}
