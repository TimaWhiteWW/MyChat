package com.TAstanov.MyChat_Recomendation.service.Impl.storage;

import com.TAstanov.MyChat_Recomendation.domain.statistic.RecommendationStatistics;
import com.TAstanov.MyChat_Recomendation.domain.user.GENDER;
import com.TAstanov.MyChat_Recomendation.domain.user.Profile;
import com.TAstanov.MyChat_Recomendation.service.RedisStatisticsService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService implements RedisStatisticsService {

    private final RedisTemplate<String, Object> redisTemplate;

    private String getKey(GENDER gender) {
        return "statistics:" + gender.name().toLowerCase();
    }

    @Override
    public void saveStatistics(GENDER gender, RecommendationStatistics stats) {
        redisTemplate.opsForValue().set(getKey(gender), stats);
        log.info("Статистика для {} сохранена в Redis.", gender);
    }

    @Override
    public void dropRedis() {
        redisTemplate.delete("*");
    }

    @Override
    public RecommendationStatistics getStatistics(GENDER gender) {
        RecommendationStatistics recommendationStatistics = (RecommendationStatistics) redisTemplate.opsForValue().get(getKey(gender));
        if (recommendationStatistics != null && recommendationStatistics.getAvgAge() != null) {
            return recommendationStatistics;
        } else if (gender == GENDER.MALE) {
            return RecommendationStatistics.averageMALE();
        } else {
            return RecommendationStatistics.averageFEMALE();
        }
    }

    @Override
    public void deleteStatistics(GENDER gender) {
        redisTemplate.delete(getKey(gender));
        log.info("Статистика для {} удалена из Redis.", gender);
    }

    public void saveUserPartnerStack(List<Profile> profiles, String tag){
        profiles.forEach(profile -> redisTemplate.opsForList().rightPush(tag, profile));
    }

    public List<Profile> getUserPartnerStack(String tag, Integer stackLength){
        List<Object> objectProfiles = redisTemplate.opsForList().rightPop(tag, stackLength);
        if(objectProfiles!=null && !objectProfiles.isEmpty()) {
            List<Profile> profiles = new ArrayList<>(80);
            Gson gson = new GsonBuilder().create();
            for (Object profileObject : objectProfiles) {
                System.out.println("Raw from Redis: " + profileObject);
                System.out.println("Class: " + profileObject.getClass().getName());
            }
            objectProfiles.forEach(profileObject -> profiles.add((Profile) profileObject));
            return profiles;
        }else{
            return null;
        }
    }
}
