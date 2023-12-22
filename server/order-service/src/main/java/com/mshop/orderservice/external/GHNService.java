package com.mshop.orderservice.external;

import com.sudo248.domain.common.Constants;
import com.mshop.orderservice.controller.order.dto.ghn.*;
import com.mshop.orderservice.repository.entity.order.ShipmentType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "GHN", url = "https://dev-online-gateway.ghn.vn/shiip/public-api/v2")
@Service
public interface GHNService {
    ShipmentType DEFAULT_SHIPMENT_TYPE = ShipmentType.STANDARD;

    @PostMapping(value = "/shipping-order/fee", headers = "token=" + Constants.GHN_TOKEN)
    GHNResponse<CalculateFeeDto> calculateFee(
            @RequestHeader("ShopId") int shopId,
            @RequestBody CalculateFeeRequest request
    );

    @PostMapping(value = "/shipping-order/leadtime", headers = "token=" + Constants.GHN_TOKEN)
    GHNResponse<CalculateExpectedTimeDto> calculateExpectedTime(
            @RequestHeader("ShopId") int shopId,
            @RequestBody CalculateExpectedTimeRequest request
    );


}
