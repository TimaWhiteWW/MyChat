package com.TAstanov.MyChat_Auth.service;

import com.TAstanov.MyChat_Auth.domain.user.User;

public interface EmailVerificationService {

    void createAndSendCode(User user);

    boolean verify(String email, String code);
}
