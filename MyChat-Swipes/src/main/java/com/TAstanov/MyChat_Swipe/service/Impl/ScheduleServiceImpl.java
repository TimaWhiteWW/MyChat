package com.TAstanov.MyChat_Swipe.service.Impl;

import com.TAstanov.MyChat_Swipe.service.PostgresService;
import com.TAstanov.MyChat_Swipe.service.RedisService;
import com.TAstanov.MyChat_Swipe.service.ScheduleService;
import com.TAstanov.MyChat_Swipe.service.props.AdjustProps;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final PostgresService postgresService;
    private final RedisService redisService;
    private final AdjustProps adjustProps;

    @Override
    @Scheduled(cron = "0 35 3 * * *")
    public void deleteExpiredLikes() {
        postgresService.deleteExpiredLikes();
    }

    @Override
    @Scheduled(cron = "0 30 3 * * *")
    @Transactional
    public void updateUserStatistics() {
        Map<String, Double> batchAdjustments;

        do {
            batchAdjustments = redisService.getAdjustedUsers();

            List<String> uniqueTags = extractUniqueTags(batchAdjustments);
            Map<String, Double> currentBeautyScores = postgresService.getAdjustedValues(uniqueTags);

            updateBeautyScores(batchAdjustments, currentBeautyScores);
            postgresService.updateBeautyScores(currentBeautyScores);

        } while (batchAdjustments.size() == adjustProps.getSaveBatchSize());
    }

    private List<String> extractUniqueTags(Map<String, Double> batchAdjustments) {
        return batchAdjustments.keySet().stream()
                .map(this::removeLikeSuffix)
                .toList();
    }

    private void updateBeautyScores(Map<String, Double> batchAdjustments, Map<String, Double> currentBeautyScores) {
        batchAdjustments.forEach((tagLike, redisValue) -> {
            String tag = removeLikeSuffix(tagLike);
            currentBeautyScores.merge(tag, redisValue, Double::sum);
        });
    }

    private String removeLikeSuffix(String tagLike) {
        return tagLike.substring(0, tagLike.length() - 4);
    }

}
