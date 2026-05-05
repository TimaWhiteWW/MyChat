package com.TAstanov.MyChat_UserMicroservice.service;

import com.TAstanov.MyChat_UserMicroservice.web.dto.UserProfileDto;

public interface UserService {

    void create(UserProfileDto userProfileDto);

}
