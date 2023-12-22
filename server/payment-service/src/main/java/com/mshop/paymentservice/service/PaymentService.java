package com.mshop.paymentservice.service;

import com.mshop.base.BaseResponse;
import com.mshop.base.BaseService;
import com.mshop.paymentservice.controller.dto.PaymentDto;
import com.mshop.paymentservice.controller.dto.PaymentInfoDto;
import org.springframework.http.ResponseEntity;


public interface PaymentService extends BaseService {
    ResponseEntity<BaseResponse<?>> pay(String userId, long currentTime, PaymentDto paymentDto);

    PaymentInfoDto getPaymentInfo(String paymentId);
}
