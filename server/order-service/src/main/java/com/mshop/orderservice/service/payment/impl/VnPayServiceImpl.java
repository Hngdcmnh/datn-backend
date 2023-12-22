package com.mshop.orderservice.service.payment.impl;

import com.sudo248.domain.base.BaseResponse;
import com.sudo248.domain.exception.ApiException;
import com.sudo248.domain.util.Utils;
import com.mshop.orderservice.config.VnPayConfig;
import com.mshop.orderservice.controller.order.dto.*;
import com.mshop.orderservice.controller.payment.dto.PaymentDto;
import com.mshop.orderservice.controller.payment.dto.PaymentInfoDto;
import com.mshop.orderservice.controller.payment.dto.VnPayResponse;
import com.mshop.orderservice.internal.CartService;
import com.mshop.orderservice.internal.NotificationService;
import com.mshop.orderservice.internal.ProductService;
import com.mshop.orderservice.repository.OrderRepository;
import com.mshop.orderservice.repository.PaymentRepository;
import com.mshop.orderservice.repository.entity.order.Order;
import com.mshop.orderservice.repository.entity.payment.Notification;
import com.mshop.orderservice.repository.entity.payment.Payment;
import com.mshop.orderservice.repository.entity.payment.PaymentStatus;
import com.mshop.orderservice.service.payment.VnpayService;
import com.mshop.orderservice.service.payment.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class VnPayServiceImpl implements PaymentService, VnpayService {

    @Value("${spring.profiles.default}")
    private String profile;

    private final PaymentRepository paymentRepository;

    private final OrderRepository orderRepository;

    private final CartService cartService;

    private final NotificationService notificationService;

    private final ProductService productService;

    private final Locale locale = new Locale("vi", "VN");

    public VnPayServiceImpl(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            CartService cartService,
            NotificationService notificationService,
            ProductService productService) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.notificationService = notificationService;
        this.productService = productService;
    }

    private boolean isDevProfile() {
        return profile.equals("dev");
    }

    public ResponseEntity<BaseResponse<?>> pay(String userId, PaymentDto paymentDto) {
        return handleException(() -> {
            if (paymentDto.getOrderId() == null) throw new ApiException(HttpStatus.BAD_REQUEST, "Require order id");
            Optional<Order> order = orderRepository.findById(paymentDto.getOrderId());

            if (order.isEmpty())
                throw new ApiException(HttpStatus.NOT_FOUND, "Not found order " + paymentDto.getOrderId());
            Payment payment = toEntity(paymentDto, order.get());
            String paymentUrl = "";
            if(paymentDto.getPaymentType().equals("VN_PAY")){

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

                Calendar cld = Calendar.getInstance(TimeZone.getTimeZone(paymentDto.getTimeZoneId()));
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                String vnp_CreateDate = formatter.format(cld.getTime());
                vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

                cld.add(Calendar.MINUTE, 15);
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
                 paymentUrl = VnPayConfig.vnp_Url + "?" + queryUrl;
            }

            order.get().setPayment(payment);
            paymentRepository.save(payment);
            updateOrderStatus(order.get());

            return BaseResponse.ok(toDto(payment, paymentUrl, paymentDto.getTimeZoneId()));
        });
    }

    private void updateOrderStatus(Order order) throws ApiException {
        updateAmountProduct(order.getUserId(), false);
        checkoutProcessingCart(order.getUserId());
    }

    private void checkoutProcessingCart(String userId) {
        cartService.checkoutProcessingCart(userId);
    }

    private void updateAmountProduct(String userId, Boolean isRestore) throws ApiException {
        CartDto cart = getProcessingCart(userId);
        for (OrderCartProductDto orderCartProductDto : cart.getCartProducts()) {
            updateAmountProduct(orderCartProductDto, isRestore);
        }
    }

    // isRestore == true => hoan lai so luong san pham
    private void updateAmountProduct(OrderCartProductDto orderCartProductDto, Boolean isRestore) throws ApiException {
        final PutAmountProductDto putAmountProductDto = new PutAmountProductDto(
                orderCartProductDto.getProduct().getProductId(),
                isRestore ? orderCartProductDto.getQuantity() : -orderCartProductDto.getQuantity()
        );
        ResponseEntity<BaseResponse<?>> response = productService.putProductAmount(putAmountProductDto);
        if (!response.hasBody()) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Some thing went wrong");
        }
        if (!Objects.requireNonNull(response.getBody()).isSuccess()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, response.getBody().getMessage());
        }
    }

    private void updateAmountPromotion(String promotionId) throws ApiException {
        final PutAmountPromotionDto promotionDto = new PutAmountPromotionDto(
                promotionId,
                -1
        );
        ResponseEntity<BaseResponse<?>> response = productService.putPromotionAmount(promotionDto);
        if (!response.hasBody()) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Some thing went wrong");
        }
        if (!Objects.requireNonNull(response.getBody()).isSuccess()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, response.getBody().getMessage());
        }
    }

    private CartDto getProcessingCart(String userId) throws ApiException {
        var response = cartService.getProcessingCart(userId);
        if (response.getStatusCode() != HttpStatus.OK || !response.hasBody())
            throw new ApiException(HttpStatus.NOT_FOUND, "Not found processing cart for user" + userId);
        return Objects.requireNonNull(response.getBody()).getData();
    }

    private List<CartProductDto> getCartProductByCartId(String cartId) throws ApiException {
        var response = cartService.getCartProductByCartId(cartId);
        if (response.getStatusCode() != HttpStatus.OK || !response.hasBody())
            throw new ApiException(HttpStatus.NOT_FOUND, "Not found cart " + cartId);
        return Objects.requireNonNull(response.getBody()).getData();
    }

    @Override
    public PaymentInfoDto getPaymentInfo(String paymentId) {
        Payment payment = paymentRepository.getReferenceById(paymentId);
        return new PaymentInfoDto(
                paymentId,
                payment.getAmount(),
                payment.getPaymentType(),
                payment.getPaymentDateTime(),
                payment.getStatus()
        );
    }

    @Override
    public PaymentInfoDto toPaymentInfoDto(Payment payment) {
        return new PaymentInfoDto(
                payment.getPaymentId(),
                payment.getAmount(),
                payment.getPaymentType(),
                payment.getPaymentDateTime(),
                payment.getStatus()
        );
    }

    @Override
    public Payment getPaymentById(String paymentId) {
        return paymentRepository.getReferenceById(paymentId);
    }

    private Payment toEntity(PaymentDto paymentDto, Order order) {
        return new Payment(
                Utils.createIdOrElse(paymentDto.getPaymentId()),
                paymentDto.getOrderType(),
                order.getFinalPrice(),
                paymentDto.getBankCode(),
                paymentDto.getPaymentType(),
                paymentDto.getIpAddress(),
                PaymentStatus.PENDING,
                paymentDto.getTimeZoneId(),
                LocalDateTime.now(ZoneId.of(paymentDto.getTimeZoneId())),
                order
        );
    }

    private PaymentDto toDto(Payment payment, String paymentUrl, String timeZoneId) {
        return new PaymentDto(
                payment.getPaymentId(),
                payment.getOrder().getOrderId(),
                payment.getOrderType(),
                payment.getBankCode(),
                payment.getAmount(),
                payment.getPaymentType(),
                payment.getIpAddress(),
                timeZoneId,
                paymentUrl,
                payment.getStatus()
        );
    }

    @Override
    public RedirectView returnVnPay(
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
            return new RedirectView(VnPayConfig.vnp_SuccessUrl);
        } else {
            return new RedirectView(VnPayConfig.vnp_FailUrl);
        }
//        } else {
//            return "payment_fail";
//        }
    }

    // vnpay call to update merchant payment
    // This API will be call to verify update payment in merchant website after that call return url
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
                vnp_PayDate,
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
            long vnp_PayDate,
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
                        // if thanh toan that bai => tra lai so luong cho san pham
                        try {
                            updateAmountProduct(payment.getOrder().getUserId(), true);
                        } catch (ApiException e) {
                            e.printStackTrace();
                        }
                    }
//                    notificationService.sendNotificationPaymentStatus(payment.getOrder().getUserId(), notification);
                    if (payment.getBankCode() == null || payment.getBankCode().isEmpty()) {
                        payment.setBankCode(bankCode);
                    }
                    payment.setPaymentDateTime(Instant.ofEpochMilli(vnp_PayDate).atZone(ZoneId.of(payment.getZoneId())).toLocalDateTime());
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
