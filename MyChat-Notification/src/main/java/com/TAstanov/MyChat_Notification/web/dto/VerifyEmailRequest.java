package com.TAstanov.MyChat_Notification.web.dto;

import lombok.Data;

@Data
public class VerifyEmailRequest {
    private String email;
    private String code;
}
