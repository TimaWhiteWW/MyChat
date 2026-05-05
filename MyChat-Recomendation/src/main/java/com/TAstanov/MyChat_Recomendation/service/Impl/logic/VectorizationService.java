package com.TAstanov.MyChat_Recomendation.service.Impl.logic;

import com.TAstanov.MyChat_Recomendation.domain.statistic.RecommendationStatistics;
import com.TAstanov.MyChat_Recomendation.domain.user.UserTemporalData;
import com.TAstanov.MyChat_Recomendation.domain.vector.VectorizedUser;
import com.TAstanov.MyChat_Recomendation.domain.vector.VectorizedUserMapper;
import com.TAstanov.MyChat_Recomendation.domain.vector.VectorizedUserSql;
import com.TAstanov.MyChat_Recomendation.repository.TemporaryRepository;
import com.TAstanov.MyChat_Recomendation.repository.VectorizedUserRepositorySql;
import com.TAstanov.MyChat_Recomendation.service.ElasticsearchService;
import com.TAstanov.MyChat_Recomendation.service.RedisStatisticsService;
import com.TAstanov.MyChat_Recomendation.service.StatisticsService;
import com.TAstanov.MyChat_Recomendation.domain.user.UserPropertiesToVectorize;
import com.TAstanov.MyChat_Recomendation.service.UserVectorizeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class VectorizationService implements UserVectorizeService {

    private final StatisticsService statisticsService;
    private final RedisStatisticsService redisStatisticsService;
    private final ElasticsearchService elasticsearchService;
    private final TemporaryRepository temporaryRepository;
    private final VectorizedUserRepositorySql vectorizedUserRepositorySql   ;
    private final VectorizedUserMapper vectorizedUserMapper;

    @Override
    public void vectorize(UserPropertiesToVectorize properties) {
        VectorizedUser vectorizedUser = vectorizeByGender(properties);
        UserTemporalData temporalData = createTemporaryData(vectorizedUser);
        VectorizedUserSql vectorizedUserSql = vectorizedUserMapper.toSqlEntity(vectorizedUser);

        temporaryRepository.save(temporalData);
        vectorizedUserRepositorySql.save(vectorizedUserSql);
        elasticsearchService.saveUser(vectorizedUser, temporalData);


        updateStatisticsWithNewUser(properties);
        log.info("Пользователь {} векторизован и сохранён", properties.getUserTag());
    }

    private VectorizedUser vectorizeByGender(UserPropertiesToVectorize user) {
        RecommendationStatistics stats = redisStatisticsService.getStatistics(user.getGender());
        if (stats == null) {
            throw new IllegalStateException("Нет статистики для пола: " + user.getGender());
        }
        if(user.getGender() == null){
            throw new IllegalStateException("Нет пользователя" + user.getUserTag());
        }

        double[] vector = buildNotNormalizedUserVector(user, stats);

        VectorizedUser vectorizedUser = new VectorizedUser();
        vectorizedUser.setUserTag(user.getUserTag());
        vectorizedUser.setGender(user.getGender());
        vectorizedUser.setVector(vector);
        return vectorizedUser;
    }

    private void updateStatisticsWithNewUser(UserPropertiesToVectorize properties) {
        RecommendationStatistics updatedStats = statisticsService.calculateNewAvg(properties);
        redisStatisticsService.saveStatistics(properties.getGender(), updatedStats);
        log.info("Обновлена статистика для пользователя {}", properties.getUserTag());
    }

    private double[] buildNotNormalizedUserVector(UserPropertiesToVectorize user, RecommendationStatistics stats) {
        // Множители, подобранные с учётом исследований предпочтений женщин
        double heightMultiplier = 32.0;
        double ageMultiplier = 2.2;
        double weightMultiplier = 2.0;
        double earningsMultiplier = 2.2;
        double beautyMultiplier = 1.0;
        double extraversionMultiplier = 0.8;
        double opennessMultiplier = 0.8;
        double conscientiousnessMultiplier = 1.0;
        double lifeValueFamilyMultiplier = 1.1;
        double lifeValueCareerMultiplier = 0.5;
        double activityLevelMultiplier = 0.5;

        return new double[]{
                user.getHeight() != null ? (user.getHeight() / stats.getAvgHeight()) * heightMultiplier : 1.0,
                user.getAge() != null ? ((double) user.getAge() / stats.getAvgAge()) * ageMultiplier : 1.0,
                user.getWeight() != null ? (user.getWeight() / stats.getAvgWeight()) * weightMultiplier : 1.0,
                user.getEarnings() != null ? ((double) user.getEarnings() / stats.getAvgEarnings()) * earningsMultiplier : 1.0,
                user.getBeauty() != null ? (user.getBeauty() / stats.getAvgBeauty()) * beautyMultiplier : 1.0,
                user.getPersonalityExtraversion() != null ? (user.getPersonalityExtraversion() / stats.getAvgPersonalityExtraversion()) * extraversionMultiplier : 1.0,
                user.getPersonalityOpenness() != null ? (user.getPersonalityOpenness() / stats.getAvgPersonalityOpenness()) * opennessMultiplier : 1.0,
                user.getPersonalityConscientiousness() != null ? (user.getPersonalityConscientiousness() / stats.getAvgPersonalityConscientiousness()) * conscientiousnessMultiplier : 1.0,
                user.getLifeValueFamily() != null ? (user.getLifeValueFamily() / stats.getAvgLifeValueFamily()) * lifeValueFamilyMultiplier : 1.0,
                user.getLifeValueCareer() != null ? (user.getLifeValueCareer() / stats.getAvgLifeValueCareer()) * lifeValueCareerMultiplier : 1.0,
                user.getActivityLevel() != null ? (user.getActivityLevel() / stats.getAvgActivityLevel()) * activityLevelMultiplier : 1.0
        };
    }


    private UserTemporalData createTemporaryData(VectorizedUser vectorize){
        Random random = new Random();
        UserTemporalData temporalData = new UserTemporalData();
        temporalData.setOffsetUsers(0);
        temporalData.setUserTag(vectorize.getUserTag());
        int tableNumber = Math.abs(vectorize.getUserTag().hashCode()) % 5 + 1;
        temporalData.setTemporaryTable(tableNumber);
        return temporalData;
    }
}
