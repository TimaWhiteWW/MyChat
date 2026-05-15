package com.TAstanov.MyChat_Auth.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyEmailRequest {

    @NotBlank(message = "email must be not blank")
    @Email(message = "it must have email pattern")
    private String email;

    @NotBlank(message = "code must be not blank")
    private String code;
}
