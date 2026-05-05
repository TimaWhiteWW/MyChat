package com.TAstanov.MyChat_Swipe.service;

import java.time.LocalDateTime;
import java.util.Map;

public interface RedisService {

    boolean likeExists(String userTag, String likedUserTag);

    void postLike(String userTag, String likedUserTag);

    void deleteLikeMessage(String firstUserTag, String secondUserTag);

    Map<String, Double> getAdjustedUsers();

    void adjustBeauty(String receivingUser);

    void postPass(String passedUserTag);

}
