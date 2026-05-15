package com.TAstanov.MyChat_Auth.service.Impl;

import com.TAstanov.MyChat_Auth.domain.user.User;
import com.TAstanov.MyChat_Auth.domain.verification.EmailVerificationCode;
import com.TAstanov.MyChat_Auth.repository.EmailVerificationCodeRepository;
import com.TAstanov.MyChat_Auth.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private static final int CODE_BOUND = 1_000_000;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final EmailVerificationCodeRepository verificationCodeRepository;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Override
    public void createAndSendCode(User user) {
        String code = String.format("%06d", RANDOM.nextInt(CODE_BOUND));

        EmailVerificationCode verificationCode = new EmailVerificationCode();
        verificationCode.setEmail(user.getEmail());
        verificationCode.setCode(code);
        verificationCode.setCreatedAt(LocalDateTime.now());
        verificationCode.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        verificationCodeRepository.save(verificationCode);

        sendCode(user, code);
    }

    @Override
    public boolean verify(String email, String code) {
        EmailVerificationCode verificationCode = verificationCodeRepository.findById(email)
                .orElseThrow(() -> new IllegalArgumentException("Verification code not found"));

        if (verificationCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Verification code expired");
        }
        if (!verificationCode.getCode().equals(code)) {
            throw new IllegalArgumentException("Invalid verification code");
        }

        verificationCodeRepository.delete(verificationCode);
        return true;
    }

    private void sendCode(User user, String code) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            log.info("Email verification code for {} is {}", user.getEmail(), code);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("MyChat email verification");
            message.setText("Your MyChat verification code: " + code);
            mailSender.send(message);
        } catch (Exception exception) {
            log.warn("Could not send verification email to {}. Code: {}", user.getEmail(), code, exception);
        }
    }
}
