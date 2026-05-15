package com.TAstanov.MyChat_Auth.service;

import com.TAstanov.MyChat_Auth.domain.user.User;

public interface AuthService {

    boolean register(User user);

    boolean verifyEmail(String email, String code);

}
