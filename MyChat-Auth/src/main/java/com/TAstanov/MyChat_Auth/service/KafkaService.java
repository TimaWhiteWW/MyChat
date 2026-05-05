package com.TAstanov.MyChat_Auth.service;

import com.TAstanov.MyChat_Auth.domain.user.User;

public interface KafkaService {

    void send(User user);

}
