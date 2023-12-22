package com.mshop.paymentservice.controller;

import com.mshop.base.BaseResponse;
import com.mshop.common.Constants;
import com.mshop.paymentservice.controller.dto.PaymentDto;
import com.mshop.paymentservice.controller.dto.PaymentInfoDto;
import com.mshop.paymentservice.service.IpService;
import com.mshop.paymentservice.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final IpService ipService;

    public PaymentController(PaymentService paymentService, IpService ipService) {
        this.paymentService = paymentService;
        this.ipService = ipService;
    }

    @PostMapping("/pay/{currentTime}")
    public ResponseEntity<BaseResponse<?>> pay(
            @RequestHeader(Constants.HEADER_USER_ID) String userId,
            @PathVariable("currentTime") long currentTime,
            @RequestBody PaymentDto paymentDto,
            HttpServletRequest request
    ) {
        if (paymentDto.getIpAddress() == null) paymentDto.setIpAddress(ipService.getIpAddress(request));
        return paymentService.pay(userId, currentTime, paymentDto);
    }

    @GetMapping("/payment-info/{paymentId}")
    public PaymentInfoDto getPaymentInfoById(@PathVariable("paymentId") String paymentId) {
        return paymentService.getPaymentInfo(paymentId);
    }
}
