package com.mshop.notification.service;


import com.mshop.notification.controller.dto.ChatDto;
import com.mshop.notification.repository.entity.Chat;
import com.mshop.notification.repository.entity.Conversation;
import com.mshop.notification.repository.entity.ConversationInfo;
import com.mshop.notification.repository.entity.User;

import java.util.List;

public interface ChatService {

    static final String conversationTopic = "public.conversation";

    List<ConversationInfo> getAllConversationInfo(String userId);

    Conversation getConversationByTopic(String userId, String topic);

    Chat sendMessageToConversation(String userId, String topic, ChatDto chatDto) throws Exception;

    User upsertUser(User user);

}