package com.mshop.orderservice.controller.order.dto.ghn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalculateExpectedTimeDto {
    @JsonProperty("leadtime")
    private int leadTime;

    @JsonProperty("order_date")
    private int orderDate;
}
