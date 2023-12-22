package com.mshop.notification.controller;

import com.mshop.base.BaseResponse;
import com.mshop.common.Constants;
import com.mshop.util.Utils;
import com.mshop.notification.repository.entity.Notification;
import com.mshop.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/token/{token}")
    public ResponseEntity<BaseResponse<?>> postToken(
            @RequestHeader(Constants.HEADER_USER_ID) String userId,
            @PathVariable("token") String token
    ) {
        return Utils.handleException(() -> BaseResponse.ok(notificationService.saveToken(userId, token)));
    }

    @PostMapping("/send/topic/{topic}")
    public ResponseEntity<BaseResponse<?>> sendNotificationToTopic(
            @RequestHeader(Constants.HEADER_USER_ID) String userId,
            @PathVariable("topic") String topic,
            @RequestBody Notification notification
    ) {
        return Utils.handleException(() -> BaseResponse.ok(notificationService.sendNotificationToTopic(userId, topic, notification)));
    }

    @PostMapping("/send/token/{token}")
    public ResponseEntity<BaseResponse<?>> sendNotificationToToken(
            @PathVariable("token") String token,
            @RequestBody Notification notification
    ) {
        return Utils.handleException(() -> BaseResponse.ok(notificationService.sendNotificationToToken(token, notification)));
    }

    @PostMapping("/send/promotion")
    public ResponseEntity<BaseResponse<?>> sendNotificationToTopicPromotion(
            @RequestHeader(Constants.HEADER_USER_ID) String userId,
            @RequestBody Notification notification
    ) {
        return Utils.handleException(() -> BaseResponse.ok(notificationService.sendNotificationToTopic(userId, NotificationService.promotionTopic, notification)));
    }

    @PostMapping("/send/payment")
    public ResponseEntity<BaseResponse<?>> sendNotificationPayment(
            @RequestHeader(Constants.HEADER_USER_ID) String userId,
            @RequestBody Notification notification
    ) {
        return Utils.handleException(() -> BaseResponse.ok(notificationService.sendNotificationPayment(userId, notification)));
    }
}
