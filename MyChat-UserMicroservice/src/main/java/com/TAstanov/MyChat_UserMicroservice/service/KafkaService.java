package com.TAstanov.MyChat_UserMicroservice.service;

import com.TAstanov.MyChat_UserMicroservice.domain.profiles.Profile;

public interface KafkaService {

    void send(Profile profile);

}
