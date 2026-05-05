package com.TAstanov.MyChat_Swipe.service.Impl;

import com.TAstanov.MyChat_Swipe.service.KafkaService;
import com.TAstanov.MyChat_Swipe.service.PostgresService;
import com.TAstanov.MyChat_Swipe.service.RedisService;
import com.TAstanov.MyChat_Swipe.service.SwipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SwipeServiceImpl implements SwipeService {

    private final RedisService redisService;
    private final PostgresService postgresService;
    private final KafkaService kafkaService;

    @Override
    public void pass(String userTag, String passingUserTag) {
        redisService.postPass(userTag);
        if(redisService.likeExists(userTag, passingUserTag)){
            redisService.deleteLikeMessage(userTag, passingUserTag);
        }
    }

    @Override
    public void like(String userTag, String likedUserTag) {

        if(redisService.likeExists(userTag, likedUserTag)){
            kafkaService.send(userTag, likedUserTag).subscribe();
            redisService.deleteLikeMessage(userTag, likedUserTag);
            redisService.adjustBeauty(likedUserTag);
            return;
        } else if(postgresService.likeExists(userTag, likedUserTag)){
            kafkaService.send(userTag, likedUserTag).subscribe();
            return;
        }

        redisService.postLike(userTag, likedUserTag);
        postgresService.postLike(userTag, likedUserTag);
    }
}
