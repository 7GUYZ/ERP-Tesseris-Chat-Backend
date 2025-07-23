package com.ict.edu03.chat.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoom {
    private String roomId;
    private String name;

    public static ChatRoom create(String name) {
        return new ChatRoom(UUID.randomUUID().toString(), name);
    }
}
