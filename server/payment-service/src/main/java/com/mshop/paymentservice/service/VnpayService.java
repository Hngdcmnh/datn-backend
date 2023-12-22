package com.mshop.paymentservice.service;

import com.mshop.paymentservice.controller.dto.VnPayResponse;

public interface VnpayService {

    String returnVnPay(
            String vnp_TmnCode,
            long vnp_Amount,
            String vnp_BankCode,
            String vnp_BankTranNo,
            String vnp_CardType,
            long vnp_PayDate,
            String vnp_OrderInfo,
            long vnp_TransactionNo,
            String vnp_ResponseCode,
            String vnp_TransactionStatus,
            String vnp_TxnRef,
            String vnp_SecureHashType,
            String vnp_SecureHash
    );

    VnPayResponse ipnVnpay(
            String vnp_TmnCode,
            long vnp_Amount,
            String vnp_BankCode,
            String vnp_BankTranNo,
            String vnp_CardType,
            long vnp_PayDate,
            String vnp_OrderInfo,
            long vnp_TransactionNo,
            String vnp_ResponseCode,
            String vnp_TransactionStatus,
            String vnp_TxnRef,
            String vnp_SecureHashType,
            String vnp_SecureHash
    );

}
