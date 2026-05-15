package com.TAstanov.MyChat_Notification.web.dto;

import lombok.Data;

@Data
public class ChatMessageDto {
    private String fromTag;
    private String toTag;
    private String text;
}
