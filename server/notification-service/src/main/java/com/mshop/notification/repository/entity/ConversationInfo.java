package com.mshop.notification.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationInfo {
    private String topic;
    private String conversationName;
    private String conversationImage;
    private String latestMessage;
    private long updateTime;
}
