package com.TAstanov.MyChat_Swipe.service.Impl;

import com.TAstanov.MyChat_Swipe.domain.Like;
import com.TAstanov.MyChat_Swipe.domain.LikeId;
import com.TAstanov.MyChat_Swipe.repository.AdjustingRepository;
import com.TAstanov.MyChat_Swipe.repository.LikesRepository;
import com.TAstanov.MyChat_Swipe.service.PostgresService;
import com.TAstanov.MyChat_Swipe.service.props.PostgresProps;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostgresServiceImpl implements PostgresService {

    private final LikesRepository likesRepository;
    private final AdjustingRepository adjustingRepository;
    private final PostgresProps postgresProps;

    @Override
    public void postLike(String userTag, String likedUserTag) {
        Like like = new Like();
        like.setUserTag(userTag);
        like.setLikedUserTag(likedUserTag);
        like.setExpirationDate(getExpirationTime(postgresProps.getExpirationDate()));
        likesRepository.save(like);
    }

    @Override
    public boolean likeExists(String userTag, String likedUserTag) {
        LikeId like = new LikeId(likedUserTag, userTag);
        return likesRepository.existsById(like);
    }

    @Override
    public void deleteExpiredLikes() {
        likesRepository.deleteExpiredLikes(LocalDateTime.now());
    }

    @Override
    public void updateBeautyScores(Map<String, Double> mapOfUsersAdjust) {
        adjustingRepository.updateAdjustedStatistics(mapOfUsersAdjust);
    }

    @Override
    public Map<String, Double> getAdjustedValues(List<String> adjustingUsersTags) {
        return adjustingRepository.getAdjustingValuesByTags(adjustingUsersTags);
    }

    private LocalDateTime getExpirationTime(Integer expiration){
        return LocalDateTime.now().plus(Duration.ofDays(expiration));
    }

}
