package com.mshop.paymentservice.internal;

import com.mshop.base.BaseResponse;
import com.mshop.common.Constants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "CART-SERVICE")
@Service
public interface CartService {
    @PutMapping("/api/v1/cart/active/completed")
    ResponseEntity<BaseResponse<?>> updateStatusCart(
            @RequestHeader(Constants.HEADER_USER_ID) String userId
    );
}
