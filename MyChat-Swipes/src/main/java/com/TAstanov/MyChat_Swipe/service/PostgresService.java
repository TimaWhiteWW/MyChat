package com.TAstanov.MyChat_Swipe.service;

import java.util.List;
import java.util.Map;

public interface PostgresService {

    void postLike(String userTag, String likedUserTag);

    boolean likeExists(String userTag, String likedUserTag);

    void deleteExpiredLikes();

    void updateBeautyScores(Map<String, Double> mapOfUsersAdjust);

    Map<String, Double> getAdjustedValues(List<String> adjustingUsersTags);

}
