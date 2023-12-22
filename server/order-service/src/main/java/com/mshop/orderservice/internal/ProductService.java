package com.mshop.orderservice.internal;

import com.sudo248.domain.base.BaseResponse;
import com.sudo248.domain.common.Constants;
import com.mshop.orderservice.controller.order.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "product-service")
@Service
public interface ProductService {
    @GetMapping("/api/v1/discovery/suppliers/{supplierId}/info")
    BaseResponse<SupplierInfoDto> getSupplierById(@PathVariable("supplierId") String supplierId);

    @GetMapping("/api/v1/discovery/suppliers/self/info")
    BaseResponse<SupplierInfoDto> getSupplierByUserId(@RequestHeader(Constants.HEADER_USER_ID) String userId);

    @GetMapping("/api/v1/promotions/{promotionId}")
    ResponseEntity<BaseResponse<PromotionDto>> getPromotionById(@PathVariable("promotionId") String promotionId);

    @PutMapping("/api/v1/discovery/products/internal/amount")
    ResponseEntity<BaseResponse<?>> putProductAmount(@RequestBody PutAmountProductDto patchProduct);

    @PutMapping("/api/v1/promotions/internal/amount")
    ResponseEntity<BaseResponse<?>> putPromotionAmount(@RequestBody PutAmountPromotionDto patchPromotion);

    @PostMapping("/api/v1/discovery/internal/user-product")
    ResponseEntity<BaseResponse<?>> upsertUserProduct(
            @RequestHeader(Constants.HEADER_USER_ID) String userId,
            @RequestBody UpsertUserProductDto upsertUserProductDto
    );

    @GetMapping("/api/v1/discovery/reviews")
    ResponseEntity<BaseResponse<?>> getReviews(
            @RequestHeader(Constants.HEADER_USER_ID) String userId
//            @RequestParam(value = "isReviewed", required = false) Boolean isReViewe = true,
//            @RequestParam(value = "offset", required = false, defaultValue = "20") int offset,
//            @RequestParam(value = "limit", required = false, defaultValue = "20") int limit
    );
}
