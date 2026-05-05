package com.TAstanov.MyChat_Auth.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LogoutRequest {

    @NotBlank(message = "You must attach access token")
    private String accessToken;
    @NotBlank(message = "You must attach refresh token")
    private String refreshToken;

}
