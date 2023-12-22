package com.mshop.notification.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chat implements Serializable {
    private UserChat sender;

    private UserChat receiver;

    private String content;

    private long timestamp;

}
