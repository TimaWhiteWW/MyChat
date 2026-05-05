package com.TAstanov.MyChat_Auth.service.Impl;

import com.TAstanov.MyChat_Auth.domain.exception.EmailAlreadyExists;
import com.TAstanov.MyChat_Auth.domain.user.User;
import com.TAstanov.MyChat_Auth.repository.AuthRepository;
import com.TAstanov.MyChat_Auth.service.AuthService;
import com.TAstanov.MyChat_Auth.service.KafkaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthRepository authRepository;
    private final KafkaService kafkaService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean register(User user) {
        checkIfEmailExists(user.getEmail());
        String encodedPassword = encodePassword(user.getPassword());
        user.setPassword(encodedPassword);
        saveUser(user);
        sendUserToKafka(user);
        return true;
    }

    private void checkIfEmailExists(String email) {
        authRepository.findByEmail(email).ifPresent(existingEmail -> {
            throw new EmailAlreadyExists("Email already exists");
        });
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private void saveUser(User user) {
        authRepository.save(user);
    }

    private void sendUserToKafka(User user) {
        kafkaService.send(user);
    }
}
