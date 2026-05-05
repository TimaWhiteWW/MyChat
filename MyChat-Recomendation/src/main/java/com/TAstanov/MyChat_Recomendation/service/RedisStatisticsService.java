package com.TAstanov.MyChat_Recomendation.service;

import com.TAstanov.MyChat_Recomendation.domain.statistic.RecommendationStatistics;
import com.TAstanov.MyChat_Recomendation.domain.user.GENDER;

public interface RedisStatisticsService {

    RecommendationStatistics getStatistics(GENDER gender);

    void deleteStatistics(GENDER gender);

    void saveStatistics(GENDER gender, RecommendationStatistics stats);

    void dropRedis();

}
