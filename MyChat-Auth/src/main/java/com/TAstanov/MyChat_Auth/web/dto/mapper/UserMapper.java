package com.TAstanov.MyChat_Auth.web.dto.mapper;

import com.TAstanov.MyChat_Auth.domain.user.User;
import com.TAstanov.MyChat_Auth.web.dto.RegisterRequest;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(RegisterRequest registerRequest) {
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setName(registerRequest.getName());
        user.setTag(registerRequest.getTag());
        user.setPassword(registerRequest.getPassword()); // Пароль должен быть уже зашифрован
        return user;
    }
}