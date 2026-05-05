package com.TAstanov.MyChat_Recomendation.service;

import com.TAstanov.MyChat_Recomendation.domain.user.UserPropertiesToVectorize;

public interface UserVectorizeService {

    void vectorize(UserPropertiesToVectorize properties);

}
