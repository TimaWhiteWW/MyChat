package com.TAstanov.MyChat_Recomendation.service;

import com.TAstanov.MyChat_Recomendation.domain.statistic.RecommendationStatistics;
import com.TAstanov.MyChat_Recomendation.domain.user.UserPropertiesToVectorize;

public interface StatisticsService {

    RecommendationStatistics calculateNewAvg(UserPropertiesToVectorize userProperties);


}
