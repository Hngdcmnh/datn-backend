package com.mshop.notification.repository.entity;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @DocumentId
    private String userId;
    private String fullName;
    private String image;
    private String token;

    public UserChat toUserChat() {
        return new UserChat(
                userId,
                fullName,
                image
        );
    }

}
