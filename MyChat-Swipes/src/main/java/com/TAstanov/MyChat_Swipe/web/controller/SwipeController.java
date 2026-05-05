package com.TAstanov.MyChat_Swipe.web.controller;

import com.TAstanov.MyChat_Swipe.service.SwipeService;
import com.TAstanov.MyChat_Swipe.web.dto.LikeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/swipe")
@RequiredArgsConstructor
public class SwipeController {

    private final SwipeService swipeService;

    @PostMapping("/pass")
    public void pass(@RequestBody LikeDto likeDto){
        String userTag = likeDto.getUserTag();
        String passingUserTag = likeDto.getLikedUserTag();
        swipeService.pass(userTag, passingUserTag);
    }

    @PostMapping("/like")
    public void like(@RequestBody LikeDto likeDto){
        String userTag = likeDto.getUserTag();
        String likedUserTag = likeDto.getLikedUserTag();
        swipeService.like(userTag, likedUserTag);
    }

}
