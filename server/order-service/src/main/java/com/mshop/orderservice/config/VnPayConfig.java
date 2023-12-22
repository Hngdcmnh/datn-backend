package com.mshop.orderservice.config;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class VnPayConfig {
    public static final String vnp_TmnCode = "BQPK5W1P";
    public static final String vnp_HashSecret = "KEYAKSSXYYFQVWBSMYACODCMZEORSKIF";
    public static final String vnp_Url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static final String vnp_Version  = "2.1.0";
    public static final String vnp_Command = "pay";
    public static Long getVnPayAmount(Double amount) {
        return (long)(amount * 100);
    }
    public static final String vnp_BankCode = "NCB";

    public static final String vnp_CurrCode = "VND";
    public static final String vnp_Locale = "vn";

    public static final String vnp_SuccessUrl = "http://success.sdk.merchantbackapp/";

    public static final String vnp_FailUrl = "http://fail.sdk.merchantbackapp/";

    public static final String vnp_ReturnUrl = "http://success.sdk.merchantbackapp/";
//    public static final String vnp_ReturnUrl = "http://fail.sdk.merchantbackapp/";
//    public static final String vnp_ReturnUrl = "https://sudo248.github.io/SOC-Service-oriented/hdv/payment_successful.html";
//    public static final String vnp_ReturnUrl = "https://sudo.eastasia.cloudapp.azure.com/api/v1/payment/vnpay/return-vnpay";

    public static String hashAllFields(Map fields) {
        List fieldNames = new ArrayList(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder sb = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) fields.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                sb.append(fieldName);
                sb.append("=");
                sb.append(fieldValue);
            }
            if (itr.hasNext()) {
                sb.append("&");
            }
        }
        return hmacSHA512(vnp_HashSecret,sb.toString());
    }

    public static String hmacSHA512(final String key, final String data) {
        try {

            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            return "";
        }
    }
}
