package com.mshop.orderservice.internal;

import com.mshop.orderservice.controller.order.dto.CartDto;
import com.mshop.orderservice.controller.order.dto.CartProductDto;
import com.mshop.base.BaseResponse;
import com.mshop.common.Constants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "cart-service")
@Service
public interface CartService {

    @GetMapping("/api/v1/carts/active")
    ResponseEntity<BaseResponse<CartDto>> getActiveCartByUserId(@RequestHeader(Constants.HEADER_USER_ID) String userId);

    @GetMapping("/api/v1/carts/{cartId}")
    ResponseEntity<BaseResponse<CartDto>> getCartById(
            @PathVariable("cartId") String cartId,
            @RequestParam("orderInfo") boolean orderInfo,
            @RequestParam("supplierId") String supplierId
    );

    @GetMapping("/api/v1/carts/internal/{cartId}/products")
    ResponseEntity<BaseResponse<List<CartProductDto>>> getCartProductByCartId(
            @PathVariable("cartId") String cartId
    );

    @GetMapping("/api/v1/carts/processing")
    ResponseEntity<BaseResponse<CartDto>> getProcessingCart(
            @RequestHeader(Constants.HEADER_USER_ID) String userId
    );

    @PostMapping("/api/v1/carts/checkout")
    ResponseEntity<BaseResponse<?>> checkoutProcessingCart(
            @RequestHeader(Constants.HEADER_USER_ID) String userId
    );

    @DeleteMapping("/api/v1/carts/processing")
    ResponseEntity<BaseResponse<?>> deleteProcessingCart(
            @RequestHeader(Constants.HEADER_USER_ID) String userId
    );

    @PostMapping("/api/v1/carts/internal/{cartId}/user-product")
    ResponseEntity<BaseResponse<List<String>>> upsertUserProductByUserAndSupplier(
            @PathVariable("cartId") String cartId,
            @RequestParam("userId")String userId,
            @RequestParam("supplierId") String supplierId
    );
}
