package com.TAstanov.MyChat_Notification.server.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final RedisTemplate<String, Object> redisTemplate;


    public SseEmitter subscribe(String userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError((e) -> emitters.remove(userId));

        return emitter;
    }


    public void sendNewNotification(String userId, String message) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().data(message));
            } catch (IOException e) {
                emitters.remove(userId);
                redisTemplate.opsForList().leftPush("pareMessage:"+userId, message);
            }
        }
    }

    public List<String> checkNotification(String userId){
        SseEmitter emitter = emitters.get(userId);
        List<String> messages = new ArrayList<>();
        String message;
        do{
            message = (String) redisTemplate.opsForList().rightPop("pareMessage:"+userId);
        }while(!(message != null && message.isEmpty()));
        return messages;
    }

}
