package com.TAstanov.MyChat_Recomendation.service.Impl.scheduler;

import com.TAstanov.MyChat_Recomendation.domain.statistic.RecommendationStatistics;
import com.TAstanov.MyChat_Recomendation.domain.user.GENDER;
import com.TAstanov.MyChat_Recomendation.domain.user.UserTemporalData;
import com.TAstanov.MyChat_Recomendation.domain.vector.VectorizedUser;
import com.TAstanov.MyChat_Recomendation.domain.vector.VectorizedUserMapper;
import com.TAstanov.MyChat_Recomendation.domain.vector.VectorizedUserSql;
import com.TAstanov.MyChat_Recomendation.repository.StatisticRepository;
import com.TAstanov.MyChat_Recomendation.repository.TemporaryRepository;
import com.TAstanov.MyChat_Recomendation.repository.VectorizedUserRepositorySql;
import com.TAstanov.MyChat_Recomendation.service.ElasticsearchService;
import com.TAstanov.MyChat_Recomendation.service.RedisStatisticsService;
import com.TAstanov.MyChat_Recomendation.service.props.ElasticsearchProps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DailyProcessingScheduler {

    private final StatisticRepository statisticRepository;
    private final RedisStatisticsService redisStatisticsService;
    private final TemporaryRepository temporaryRepository;
    private final ElasticsearchService elasticsearchService;
    private final VectorizedUserRepositorySql vectorizedUserRepositorySql;
    private final VectorizedUserMapper vectorizedUserMapper;
    private final ElasticsearchProps elasticsearchProps;

    @Scheduled(cron = "0 0 0 * * *")
    public void saveNewAverageUser() {
        RecommendationStatistics statisticsMALE = redisStatisticsService.getStatistics(GENDER.MALE);
        RecommendationStatistics statisticsFEMALE = redisStatisticsService.getStatistics(GENDER.FEMALE);

        statisticRepository.saveOrUpdate(statisticsMALE);
        statisticRepository.saveOrUpdate(statisticsFEMALE);
        log.info("Ежедневное сохранение статистики завершено");
    }

    @Scheduled(cron = "0 0 3 * * *") // Запуск каждый день в 2 AM
    @Transactional
    public void setNewTemporaryData() {
        Random random = new Random();
        int offset = 0;
        int BATCH_SIZE = 5000;
        List<UserTemporalData> batch;

        while (!(batch = temporaryRepository.findAllWithOffsetAndLimit(offset, BATCH_SIZE)).isEmpty()) {
            batch.forEach(user -> {
                user.setTemporaryTable(random.nextInt(elasticsearchProps.getMaxShardNumber()) + 1);
                user.setOffsetUsers(0);
            });
            temporaryRepository.saveAll(batch);
            offset += BATCH_SIZE;
        }

        elasticsearchService.dropAll();
        processAndSaveToElasticsearch();
    }

    private void processAndSaveToElasticsearch() {
        int offset = 0;
        int BATCH_SIZE = 1000;
        List<UserTemporalData> batch;

        while (!(batch = temporaryRepository.findAllWithOffsetAndLimit(offset, BATCH_SIZE)).isEmpty()) {
            List<VectorizedUser> users = batch.parallelStream().map(user -> {
                VectorizedUserSql sqlUser = vectorizedUserRepositorySql.getByUserTag(user.getUserTag());
                return vectorizedUserMapper.toElasticsearchEntity(sqlUser);
            }).collect(Collectors.toList());

            elasticsearchService.saveAllUsers(users, batch);
            offset += BATCH_SIZE;
        }
    }

    @Scheduled(cron = "0 30 3 * * *")
    public void deleteRedisStats(){
        redisStatisticsService.dropRedis();
    }
}
