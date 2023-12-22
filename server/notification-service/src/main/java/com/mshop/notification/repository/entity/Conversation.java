package com.mshop.notification.repository.entity;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Conversation {

    @DocumentId
    private String topic;
    private String conversationName;
    private String conversationImage;
    private String firstUserId;
    private String secondUserId;
    private List<Chat> chats;
    private long updateTime;

    public ConversationInfo toConversationInfo() {
        String latestMessage = "";
        long updateTime = System.currentTimeMillis();
        if (chats.size() > 0) {
            latestMessage = chats.get(chats.size() - 1).getContent();
            updateTime = chats.get(chats.size() - 1).getTimestamp();
        }
        return new ConversationInfo(
                topic,
                conversationName,
                conversationImage,
                latestMessage,
                updateTime
        );
    }

    public ConversationInfo toConversationInfo(String nameConversation) {
        String latestMessage = "";
        long updateTime = System.currentTimeMillis();
        if (chats.size() > 0) {
            latestMessage = chats.get(chats.size() - 1).getContent();
            updateTime = chats.get(chats.size() - 1).getTimestamp();
        }
        return new ConversationInfo(
                topic,
                nameConversation,
                conversationImage,
                latestMessage,
                updateTime
        );
    }
}
