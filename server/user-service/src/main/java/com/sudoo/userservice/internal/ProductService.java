package com.sudoo.userservice.internal;

import com.sudo248.domain.base.BaseResponse;
import com.sudo248.domain.common.Constants;
import com.sudoo.userservice.controller.dto.UpsertUserProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "product-service")
@Service
public interface ProductService {
    @PostMapping("/api/v1/discovery/internal/user-product/all")
    ResponseEntity<BaseResponse<Boolean>> upsertUserProductForAllProduct(
            @RequestHeader(Constants.HEADER_USER_ID) String userId
    );
}
