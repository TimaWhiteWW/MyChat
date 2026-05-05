package com.TAstanov.MyChat_Recomendation.service.Impl.logic;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;


@Component
@RequiredArgsConstructor
public class RedisBatchExecutor {

    private final RedisTemplate<String, Object> redisTemplate;


    public void createDataPipelined(Map<String, String> map){
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            RedisStringCommands stringCommands = connection.stringCommands();
            for(Map.Entry<String, String> v : map.entrySet()){
                stringCommands.set(v.getKey().getBytes(), v.getValue().getBytes());
            }
            return null;
        }, new StringRedisSerializer());

        Set<String> keys = map.keySet();
        List<Object> values = redisTemplate.opsForValue().multiGet(keys);
    }

    public void createDataTransactional(Map<String, String> map){
        redisTemplate.executePipelined(new SessionCallback<List>(){
            @Override
            public List<String> execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                for(Map.Entry<String, String> v : map.entrySet()){
                    operations.opsForValue().set(v.getKey().getBytes(), v.getValue().getBytes());
                }
                return operations.exec();
            }
        });
    }

}
