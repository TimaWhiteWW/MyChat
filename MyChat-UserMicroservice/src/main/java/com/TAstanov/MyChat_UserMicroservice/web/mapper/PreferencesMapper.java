package com.TAstanov.MyChat_UserMicroservice.web.mapper;


import com.TAstanov.MyChat_UserMicroservice.domain.preferences.Preferences;
import com.TAstanov.MyChat_UserMicroservice.web.dto.PreferencesDto;
import org.springframework.stereotype.Component;

@Component
public class PreferencesMapper {

    public static Preferences toEntity(PreferencesDto preferencesDto) {
        Preferences preferences = new Preferences();
        preferences.setUserTag(preferencesDto.getUserTag());
        preferences.setPreferencesHobby(preferencesDto.getPreferencesHobby());
        preferences.setPreferencesProfessions(preferencesDto.getPreferencesProfessions());
        preferences.setMinEarnings(preferencesDto.getMinEarnings());
        preferences.setMinBMI(preferencesDto.getMinBMI());
        preferences.setMaxBMI(preferencesDto.getMaxBMI());
        preferences.setMinHeight(preferencesDto.getMinHeight());
        preferences.setMaxHeight(preferencesDto.getMaxHeight());
        preferences.setMinAge(preferencesDto.getMinAge());
        preferences.setMaxAge(preferencesDto.getMaxAge());
        preferences.setDistance(preferencesDto.getDistance());
        return preferences;
    }

    public static PreferencesDto toDto(Preferences preferences) {
        PreferencesDto preferencesDto = new PreferencesDto();
        preferencesDto.setUserTag(preferences.getUserTag());
        preferencesDto.setPreferencesHobby(preferences.getPreferencesHobby());
        preferencesDto.setPreferencesProfessions(preferences.getPreferencesProfessions());
        preferencesDto.setMinEarnings(preferences.getMinEarnings());
        preferencesDto.setMinBMI(preferences.getMinBMI());
        preferencesDto.setMaxBMI(preferences.getMaxBMI());
        preferencesDto.setMinHeight(preferences.getMinHeight());
        preferencesDto.setMaxHeight(preferences.getMaxHeight());
        preferencesDto.setMinAge(preferences.getMinAge());
        preferencesDto.setMaxAge(preferences.getMaxAge());
        preferencesDto.setDistance(preferences.getDistance());
        return preferencesDto;
    }
}
