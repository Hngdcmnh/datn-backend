package com.mshop.notification.service;

import com.mshop.notification.controller.dto.SupplierInfoDto;
import com.mshop.base.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "product-service")
@Service
public interface DiscoveryService {
    @GetMapping("/api/v1/discovery/supplier/info/{supplierId}")
    ResponseEntity<BaseResponse<SupplierInfoDto>> getSupplierInfoById(@PathVariable("supplierId") String supplierId);
}
