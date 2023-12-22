package com.mshop.orderservice.internal;

import com.mshop.base.BaseResponse;
import com.mshop.common.Constants;
import com.mshop.orderservice.repository.entity.Role;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "account-service")
@Service
public interface AccountService {
    @GetMapping("/api/v1/auth/internal/accounts/role")
    ResponseEntity<BaseResponse<Role>> getRoleByUserId(@RequestHeader(Constants.HEADER_USER_ID) String userId);
}
