package com.TAstanov.MyChat_Auth.domain.verification;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "email_verification_codes")
public class EmailVerificationCode {

    @Id
    private String email;
    private String code;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}
