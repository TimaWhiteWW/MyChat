package com.TAstanov.MyChat_Auth.service.Impl;

import com.TAstanov.MyChat_Auth.domain.user.User;
import com.TAstanov.MyChat_Auth.service.KafkaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Service
@RequiredArgsConstructor
public class KafkaServiceImpl implements KafkaService {

    private final KafkaSender<String, Object> kafkaSender;

    @Override
    public void send(User user) {
        kafkaSender.send(
                Mono.just(
                        SenderRecord.create(
                                "auth-register",
                                0,
                                System.currentTimeMillis(),
                                String.valueOf(user.hashCode()),
                                user,
                                null
                        )
                )
        ).subscribe();
    }
}
