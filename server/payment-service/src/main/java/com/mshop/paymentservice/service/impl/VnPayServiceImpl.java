package com.mshop.paymentservice.service.impl;

import com.mshop.base.BaseResponse;
import com.mshop.util.Utils;
import com.mshop.paymentservice.config.VnPayConfig;
import com.mshop.paymentservice.controller.dto.PaymentDto;
import com.mshop.paymentservice.controller.dto.PaymentInfoDto;
import com.mshop.paymentservice.controller.dto.VnPayResponse;
import com.mshop.paymentservice.internal.CartService;
import com.mshop.paymentservice.internal.NotificationService;
import com.mshop.paymentservice.internal.OrderService;
import com.mshop.paymentservice.repository.PaymentRepository;
import com.mshop.paymentservice.repository.entity.Notification;
import com.mshop.paymentservice.repository.entity.Payment;
import com.mshop.paymentservice.repository.entity.PaymentStatus;
import com.mshop.paymentservice.service.PaymentService;
import com.mshop.paymentservice.service.VnpayService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VnPayServiceImpl implements PaymentService, VnpayService {
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    private final CartService cartService;

    private final NotificationService notificationService;

    private final Locale locale = new Locale("vi", "VN");

    public VnPayServiceImpl(PaymentRepository paymentRepository, OrderService orderService, CartService cartService, NotificationService notificationService) {
        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
        this.cartService = cartService;
        this.notificationService = notificationService;
    }

    public ResponseEntity<BaseResponse<?>> pay(String userId, long currentTime, PaymentDto paymentDto) {
        return handleException(() -> {
            Payment payment = toEntity(userId, paymentDto);
            paymentRepository.save(payment);

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", VnPayConfig.vnp_Version);
            vnp_Params.put("vnp_Command", VnPayConfig.vnp_Command);
            vnp_Params.put("vnp_TmnCode", VnPayConfig.vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(VnPayConfig.getVnPayAmount(payment.getAmount())));
            vnp_Params.put("vnp_CurrCode", VnPayConfig.vnp_CurrCode);

            if (payment.getBankCode() != null && !payment.getBankCode().isEmpty()) {
                vnp_Params.put("vnp_BankCode", payment.getBankCode());
            }

            vnp_Params.put("vnp_TxnRef", payment.getPaymentId());
            vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + payment.getPaymentId());
            vnp_Params.put("vnp_OrderType", payment.getOrderType());
            vnp_Params.put("vnp_Locale", VnPayConfig.vnp_Locale);
            vnp_Params.put("vnp_ReturnUrl", VnPayConfig.vnp_ReturnUrl);
            vnp_Params.put("vnp_IpAddr", paymentDto.getIpAddress());

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7" ));
            //cld.setTimeInMillis(currentTime);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss" );
            String vnp_CreateDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            cld.add(Calendar.HOUR, +15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());

            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            Iterator<String> itr = fieldNames.iterator();

            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    //Build hash data
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }

            String queryUrl = query.toString();
            String vnp_SecureHash = VnPayConfig.hmacSHA512(VnPayConfig.vnp_HashSecret, hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            String paymentUrl = VnPayConfig.vnp_Url + "?" + queryUrl;
            orderService.updateInvoice(payment.getOrderId(), payment.getPaymentId());
            cartService.updateStatusCart(userId);
            return BaseResponse.ok(toDto(payment, paymentUrl));
        });
    }

    @Override
    public PaymentInfoDto getPaymentInfo(String paymentId) {
        Payment payment = paymentRepository.getReferenceById(paymentId);
        return new PaymentInfoDto(
                paymentId,
                payment.getAmount(),
                payment.getPaymentType()
        );
    }

    private Payment toEntity(String userId, PaymentDto paymentDto) {
        return new Payment(
                Utils.createIdOrElse(paymentDto.getPaymentId()),
                paymentDto.getOrderId(),
                userId,
                paymentDto.getOrderType(),
                paymentDto.getAmount(),
                paymentDto.getBankCode(),
                paymentDto.getPaymentType(),
                paymentDto.getIpAddress(),
                PaymentStatus.PENDING
        );
    }

    private PaymentDto toDto(Payment payment, String paymentUrl) {
        return new PaymentDto(
                payment.getPaymentId(),
                payment.getOrderId(),
                payment.getOrderType(),
                payment.getBankCode(),
                payment.getAmount(),
                payment.getPaymentType(),
                payment.getIpAddress(),
                paymentUrl,
                payment.getStatus()
        );
    }

    @Override
    public String returnVnPay(
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
    ) {
        Map<String, String> fields = new HashMap<>();
        fields.put("vnp_TmnCode", vnp_TmnCode);
        fields.put("vnp_Amount", String.valueOf(vnp_Amount));
        fields.put("vnp_BankCode", vnp_BankCode);
        fields.put("vnp_BankTranNo", vnp_BankTranNo);
        fields.put("vnp_CardType", vnp_CardType);
        fields.put("vnp_PayDate", String.valueOf(vnp_PayDate));
        fields.put("vnp_OrderInfo", vnp_OrderInfo);
        fields.put("vnp_TransactionNo", String.valueOf(vnp_TransactionNo));
        fields.put("vnp_ResponseCode", vnp_ResponseCode);
        fields.put("vnp_TransactionStatus", vnp_TransactionStatus);
        fields.put("vnp_TxnRef", vnp_TxnRef);

//        String signValue = VnPayConfig.hashAllFields(fields);

//        if (signValue.equals(vnp_SecureHash)) {
            if ("00".equals(vnp_TransactionStatus)) {
                return "payment_successful";
            } else {
                return "payment_fail";
            }
//        } else {
//            return "payment_fail";
//        }
    }

    @Override
    public VnPayResponse ipnVnpay(
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
    ) {
        Map<String, String> fields = new HashMap<>();
        fields.put("vnp_TmnCode", vnp_TmnCode);
        fields.put("vnp_Amount", String.valueOf(vnp_Amount));
        fields.put("vnp_BankCode", vnp_BankCode);
        fields.put("vnp_BankTranNo", vnp_BankTranNo);
        fields.put("vnp_CardType", vnp_CardType);
        fields.put("vnp_PayDate", String.valueOf(vnp_PayDate));
        fields.put("vnp_OrderInfo", vnp_OrderInfo);
        fields.put("vnp_TransactionNo", String.valueOf(vnp_TransactionNo));
        fields.put("vnp_ResponseCode", vnp_ResponseCode);
        fields.put("vnp_TransactionStatus", vnp_TransactionStatus);
        fields.put("vnp_TxnRef", vnp_TxnRef);

//        String signValue = VnPayConfig.hashAllFields(fields);

        VnPayResponse response;

//        if (signValue.equals(vnp_SecureHash)) {
            response = checkAndUpdatePayment(
                    vnp_TxnRef,
                    vnp_Amount,
                    vnp_BankCode,
                    vnp_TransactionStatus,
                    vnp_ResponseCode
            );
//        } else {
//            response = new VnPayResponse(
//                    "99",
//                    "Unknown error"
//            );
//        }
        return response;
    }

    private VnPayResponse checkAndUpdatePayment(
            String paymentId,
            long amount,
            String bankCode,
            String paymentStatus,
            String responseCode
    ) {

        if (paymentRepository.existsById(paymentId)) {
            Payment payment = paymentRepository.getReferenceById(paymentId);

            if ((long) (payment.getAmount() * 100) == amount) {

                if ("00".equals(paymentStatus) || payment.getStatus() == PaymentStatus.PENDING) {

                    Notification notification;

                    if ("00".equals(responseCode)) {
                        payment.setStatus(PaymentStatus.SUCCESS);
                        notification = new Notification(
                                null,
                                "Thanh toán thành công",
                                "Thanh toán thành công: " + NumberFormat.getCurrencyInstance(locale).format(payment.getAmount())
                        );
                    } else {
                        payment.setStatus(PaymentStatus.FAILURE);
                        notification = new Notification(
                                null,
                                "Thanh toán thất bại",
                                "Có lỗi xảy ra trong quá trình thanh toán. Hãy kiểm tra lại"
                        );
                    }
                    notificationService.sendNotificationPaymentStatus(payment.getUserId(), notification);
                    if (payment.getBankCode() == null || payment.getBankCode().isEmpty()) {
                        payment.setBankCode(bankCode);
                    }
                    paymentRepository.save(payment);
                    return new VnPayResponse(
                            "00",
                            "Confirm Success"
                    );

                } else {
                    return new VnPayResponse(
                            "02",
                            "Payment already confirmed"
                    );
                }

            } else {
                return new VnPayResponse(
                        "04",
                        "Invalid Amount"
                );
            }

        } else {
            return new VnPayResponse(
                    "01",
                    "Payment not Found"
            );
        }
    }
}
