package com.TAstanov.MyChat_Auth.repository;

import com.TAstanov.MyChat_Auth.domain.verification.EmailVerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationCodeRepository extends JpaRepository<EmailVerificationCode, String> {
}
