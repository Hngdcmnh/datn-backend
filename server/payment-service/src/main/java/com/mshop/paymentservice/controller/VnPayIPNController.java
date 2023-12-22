package com.mshop.paymentservice.controller;

import com.mshop.paymentservice.controller.dto.VnPayResponse;
import com.mshop.paymentservice.service.VnpayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payment/vnpay")
@Slf4j
public class VnPayIPNController {

    private final VnpayService vnpayService;

    public VnPayIPNController(VnpayService vnpayService) {
        this.vnpayService = vnpayService;
    }

    @GetMapping("/ipn-vnpay")
    public VnPayResponse ipnVnpay(
            @RequestParam(value = "vnp_TmnCode", required = false) String vnp_TmnCode,
            @RequestParam(value = "vnp_Amount", required = false) long vnp_Amount,
            @RequestParam(value = "vnp_BankCode", required = false) String vnp_BankCode,
            @RequestParam(value = "vnp_BankTranNo", required = false) String vnp_BankTranNo,
            @RequestParam(value = "vnp_CardType", required = false) String vnp_CardType,
            @RequestParam(value = "vnp_PayDate", required = false) long vnp_PayDate,
            @RequestParam(value = "vnp_OrderInfo", required = false) String vnp_OrderInfo,
            @RequestParam(value = "vnp_TransactionNo", required = false) long vnp_TransactionNo,
            @RequestParam(value = "vnp_ResponseCode", required = false) String vnp_ResponseCode,
            @RequestParam(value = "vnp_TransactionStatus", required = false) String vnp_TransactionStatus,
            @RequestParam(value = "vnp_TxnRef", required = false) String vnp_TxnRef,
            @RequestParam(value = "vnp_SecureHashType", required = false) String vnp_SecureHashType,
            @RequestParam(value = "vnp_SecureHash", required = false) String vnp_SecureHash
    ) {
        log.error("mshop: " + "vnpay server call ipn");
        return vnpayService.ipnVnpay(
                vnp_TmnCode,
                vnp_Amount,
                vnp_BankCode,
                vnp_BankTranNo,
                vnp_CardType,
                vnp_PayDate,
                vnp_OrderInfo,
                vnp_TransactionNo,
                vnp_ResponseCode,
                vnp_TransactionStatus,
                vnp_TxnRef,
                vnp_SecureHashType,
                vnp_SecureHash
        );
    }
}
