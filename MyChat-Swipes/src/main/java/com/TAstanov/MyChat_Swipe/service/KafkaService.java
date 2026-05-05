package com.TAstanov.MyChat_Swipe.service;

import reactor.core.publisher.Mono;

public interface KafkaService {

    Mono<Void> send(String userTag, String likedUserTag);

}
