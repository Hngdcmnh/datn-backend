package com.mshop.orderservice.controller.payment;

import com.sudo248.domain.base.BaseResponse;
import com.sudo248.domain.common.Constants;
import com.mshop.orderservice.controller.payment.dto.PaymentDto;
import com.mshop.orderservice.controller.payment.dto.PaymentInfoDto;
import com.mshop.orderservice.service.payment.IpService;
import com.mshop.orderservice.service.payment.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final IpService ipService;

    public PaymentController(PaymentService paymentService, IpService ipService) {
        this.paymentService = paymentService;
        this.ipService = ipService;
    }

    @PostMapping("/pay")
    public ResponseEntity<BaseResponse<?>> pay(
            @RequestHeader(Constants.HEADER_USER_ID) String userId,
            @RequestBody PaymentDto paymentDto,
            HttpServletRequest request
    ) {
        if (paymentDto.getIpAddress() == null) paymentDto.setIpAddress(ipService.getIpAddress(request));
        if (paymentDto.getTimeZoneId() == null) paymentDto.setTimeZoneId(RequestContextUtils.getTimeZone(request).getID());
        return paymentService.pay(userId, paymentDto);
    }

    @GetMapping("/payment-info/{paymentId}")
    public PaymentInfoDto getPaymentInfoById(@PathVariable("paymentId") String paymentId) {
        return paymentService.getPaymentInfo(paymentId);
    }
}
