package com.mshop.notification.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatNotification {
    private String topic;
    private String senderId;
    private String senderName;
}
