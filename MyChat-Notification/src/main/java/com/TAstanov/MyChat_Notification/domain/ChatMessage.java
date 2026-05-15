package com.TAstanov.MyChat_Notification.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ChatMessage {
    private String fromTag;
    private String toTag;
    private String text;
    private LocalDateTime createdAt;
}
