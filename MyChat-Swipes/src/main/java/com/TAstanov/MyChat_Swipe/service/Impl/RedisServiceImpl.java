package com.TAstanov.MyChat_Swipe.service.Impl;

import com.TAstanov.MyChat_Swipe.service.RedisService;
import com.TAstanov.MyChat_Swipe.service.props.AdjustProps;
import com.TAstanov.MyChat_Swipe.service.props.RedisProps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final AdjustProps adjustProps;
    private final RedisProps redisProps;

    @Override
    public boolean likeExists(String userTag, String likedUserTag) {
        return redisTemplate.opsForValue().get(likedUserTag + userTag) != null;
    }

    @Override
    public void postLike(String userTag, String likedUserTag) {
        adjustBeauty(likedUserTag);
        redisTemplate.opsForValue().set(userTag + likedUserTag, true, redisProps.getExpirationHours(), TimeUnit.HOURS);
    }

    @Override
    public void deleteLikeMessage(String firstUserTag, String secondUserTag) {
        redisTemplate.delete(firstUserTag+secondUserTag);
        redisTemplate.delete(secondUserTag+firstUserTag);
    }

    @Override
    public void adjustBeauty(String receivingUser) {
        receivingUser = receivingUser+"like";
        Object value = redisTemplate.opsForValue().get(receivingUser);
        double currentAdjust = (value instanceof Double) ? (Double) value : 0.0;
        redisTemplate.opsForValue().set(receivingUser, currentAdjust + adjustProps.getLikeUniversalAdjust(), redisProps.getExpirationHours(), TimeUnit.HOURS);
    }

    @Override
    public void postPass(String passedUserTag) {
        passedUserTag = passedUserTag+"like";
        Object value = redisTemplate.opsForValue().get(passedUserTag);
        double currentAdjust = (value instanceof Double) ? (Double) value : 0.0;
        redisTemplate.opsForValue().set(passedUserTag, currentAdjust + adjustProps.getSkipUniversalAdjust(), redisProps.getExpirationHours(), TimeUnit.HOURS);
    }

    @Override
    public Map<String, Double> getAdjustedUsers() {

        Set<String> keys = new LinkedHashSet<>();

        ScanOptions options = ScanOptions.scanOptions().match("*like").count(100).build();

        try (Cursor<byte[]> cursor = redisTemplate.executeWithStickyConnection(
                redisConnection -> redisConnection.keyCommands().scan(options))) {
            while (cursor.hasNext() && keys.size() < adjustProps.getSaveBatchSize()) {
                keys.add(new String(cursor.next(), StandardCharsets.UTF_8));
            }
        }

        List<Object> resultObjectList = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            RedisStringCommands stringCommands = connection.stringCommands();
            for (String key : keys) {
                stringCommands.getDel(key.getBytes(StandardCharsets.UTF_8));
            }
            return null;
        });


        return getResultAdjustedBatch(keys, resultObjectList);
    }

    private static Map<String, Double> getResultAdjustedBatch(Set<String> keys, List<Object> resultObjectList) {
        Map<String, Double> result = new HashMap<>();
        int index = 0;

        for (String key : keys) {
            Object valueObj = resultObjectList.get(index);
            if (valueObj instanceof Double) {
                result.put(key, (Double) valueObj);
            } else if (valueObj instanceof String) {
                try {
                    result.put(key, Double.parseDouble((String) valueObj));
                } catch (NumberFormatException e) {
                    log.error("Error while parsing result for key '{}' with value '{}'. Exception: {}", key, valueObj, e.getMessage());
                }
            } else {
                log.warn("Unexpected data type for key '{}': {}", key, valueObj != null ? valueObj.getClass().getSimpleName() : "null");
            }
            index++;
        }

        return result;
    }


}
