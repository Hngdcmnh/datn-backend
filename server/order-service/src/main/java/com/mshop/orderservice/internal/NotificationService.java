package com.mshop.orderservice.internal;

import com.mshop.orderservice.repository.entity.payment.Notification;
import com.mshop.base.BaseResponse;
import com.mshop.common.Constants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "notification-service")
@Service
public interface NotificationService {
    @PostMapping("/api/v1/notification/send/payment")
    ResponseEntity<BaseResponse<?>> sendNotificationPaymentStatus(@RequestHeader(Constants.HEADER_USER_ID) String userId, @RequestBody Notification notification);
}
