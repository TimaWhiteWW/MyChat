package com.TAstanov.MyChat_Recomendation.service;

import com.TAstanov.MyChat_Recomendation.domain.user.Profile;

import java.util.List;

public interface SelectPartnerStackService {

    List<Profile> formPartnerStack(String RequestUserTag);

}
