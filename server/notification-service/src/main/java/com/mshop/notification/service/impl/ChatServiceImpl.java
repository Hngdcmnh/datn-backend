package com.mshop.notification.service.impl;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.gson.Gson;
import com.mshop.notification.service.ChatService;
import com.mshop.notification.controller.dto.ChatDto;
import com.mshop.notification.controller.dto.SupplierInfoDto;
import com.mshop.notification.repository.ChatRepository;
import com.mshop.notification.repository.UserRepository;
import com.mshop.notification.repository.entity.Chat;
import com.mshop.notification.repository.entity.Conversation;
import com.mshop.notification.repository.entity.ConversationInfo;
import com.mshop.notification.repository.entity.User;
import com.mshop.notification.service.DiscoveryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;

    private final UserRepository userRepository;

    private final FirebaseMessaging firebaseMessaging;

    private final DiscoveryService discoveryService;

    private final Gson gson;

    public ChatServiceImpl(ChatRepository chatRepository, UserRepository userRepository, FirebaseMessaging firebaseMessaging, DiscoveryService discoveryService, Gson gson) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.firebaseMessaging = firebaseMessaging;
        this.discoveryService = discoveryService;
        this.gson = gson;
    }

    @Override
    public List<ConversationInfo> getAllConversationInfo(String userId) {
        Map<String, String> userNames = new HashMap<>();
        Map<String, String> userImage = new HashMap<>();
        return chatRepository.getConversation(userId).stream().map(conversation -> {
                    String conversationName = userNames.get(conversation.getSecondUserId());
                    if (conversationName == null) {
                        User user = userRepository.get(conversation.getSecondUserId()).get();
                        userNames.put(conversation.getSecondUserId(), user.getFullName());
                        userImage.put(conversation.getSecondUserId(), user.getImage());
                    }
                    conversation.setConversationName(userNames.get(conversation.getSecondUserId()));
                    conversation.setConversationImage(userImage.get(conversation.getSecondUserId()));
                    return conversation.toConversationInfo();
                }).sorted(Comparator.comparing(ConversationInfo::getUpdateTime))
                .collect(Collectors.toList());
    }

    @Override
    public Conversation getConversationByTopic(String userId, String topic) {
        Optional<Conversation> conversation = chatRepository.get(topic);
        Conversation conversationValue;
        if (conversation.isPresent()) {
            conversationValue = conversation.get();
        } else {
            String saveConversationName = topic.substring(topic.indexOf('%') + 1);
            String[] userIds = saveConversationName.split("%");
            SupplierInfoDto supplier = Objects.requireNonNull(discoveryService.getSupplierInfoById(userIds[0]).getBody()).getData();
            conversationValue = new Conversation(
                    topic,
                    saveConversationName,
                    "user_default.png",
                    supplier.getUserId(),
                    userIds[1],
                    new ArrayList<>(),
                    System.currentTimeMillis()
            );
            chatRepository.save(conversationValue);
        }

        if (userId.equals(conversationValue.getFirstUserId())) {
            User secondUSer = userRepository.get(conversationValue.getSecondUserId()).get();
            conversationValue.setConversationName(
                    secondUSer.getFullName()
            );
            conversationValue.setConversationImage(
                    secondUSer.getImage()
            );
        } else {
            User firstUSer = userRepository.get(conversationValue.getFirstUserId()).get();
            conversationValue.setConversationName(
                    firstUSer.getFullName()
            );
            conversationValue.setConversationImage(
                    firstUSer.getImage()
            );
        }
        return conversationValue;

    }

    @Override
    public Chat sendMessageToConversation(String userId, String topic, ChatDto chatDto) throws Exception {
        var conversation = chatRepository.get(topic);
        if (conversation.isPresent()) {
            Conversation conversationValue = conversation.get();
            Chat chat = new Chat(
                    userRepository.get(userId).get().toUserChat(),
                    userRepository.get(
                            conversationValue.getFirstUserId().equals(userId) ?
                                    conversationValue.getSecondUserId() :
                                    conversationValue.getFirstUserId()).get().toUserChat(),
                    chatDto.getContent(),
                    System.currentTimeMillis());
            conversationValue.getChats().add(chat);
            chatRepository.save(conversationValue);
            sendNotificationToTopic(topic, chat);
            return chat;
        } else {
            throw new Exception("Not found conversation");
        }
    }

    @Override
    public User upsertUser(User user) {
        userRepository.save(user);
        return user;
    }

    public String sendNotificationToTopic(String topic, Chat chat) {
        log.info("send to " + topic + " " + chat);
        List<Message> messages = new ArrayList<>();
        messages.add(
                Message.builder()
                        .setTopic(topic)
                        .setNotification(
                                Notification.builder()
                                        .setTitle(chat.getSender().getFullName())
                                        .setBody(chat.getContent())
                                        .build()
                        )
                        .putData("chat", gson.toJson(chat))
                        .build()
        );
        messages.add(
                Message.builder()
                        .setTopic(conversationTopic)
                        .setNotification(
                                Notification.builder()
                                        .setTitle(chat.getSender().getFullName())
                                        .setBody(chat.getContent())
                                        .build()
                        ).build()
        );
        firebaseMessaging.sendAllAsync(messages);
        return "Success";
    }

    public String sendNotificationToToken(String token, Chat chat) {
        Message message = Message.builder().setToken(token).setNotification(Notification.builder().setTitle(chat.getSender().getFullName()).setBody(chat.getContent()).build()).putData("chat", gson.toJson(chat)).build();
        firebaseMessaging.sendAsync(message);
        return "Success";
    }
}


