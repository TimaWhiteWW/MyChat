package com.TAstanov.MyChat_Recomendation.service.Impl.logic;

import com.TAstanov.MyChat_Recomendation.domain.statistic.RecommendationStatistics;
import com.TAstanov.MyChat_Recomendation.service.RedisStatisticsService;
import com.TAstanov.MyChat_Recomendation.domain.user.UserPropertiesToVectorize;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsService implements com.TAstanov.MyChat_Recomendation.service.StatisticsService {

    private final RedisStatisticsService redisStatisticsService;

    @Override
    public RecommendationStatistics calculateNewAvg(UserPropertiesToVectorize userProperties) {
        RecommendationStatistics statistics = redisStatisticsService.getStatistics(userProperties.getGender());
        statistics.multiplyByNumber();
        statistics.add(userProperties);
        statistics.divideByNumber();
        log.info("Пересчитана статистика для пользователя {}", userProperties.getUserTag());
        return statistics;
    }
}
