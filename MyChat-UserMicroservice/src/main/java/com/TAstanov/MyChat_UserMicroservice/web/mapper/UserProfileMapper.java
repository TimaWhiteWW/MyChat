package com.TAstanov.MyChat_UserMicroservice.web.mapper;

import com.TAstanov.MyChat_UserMicroservice.domain.location.Location;
import com.TAstanov.MyChat_UserMicroservice.domain.profiles.Profile;
import com.TAstanov.MyChat_UserMicroservice.web.dto.KafkaDto;
import com.TAstanov.MyChat_UserMicroservice.web.dto.UserProfileDto;
import org.springframework.stereotype.Component;

@Component
public class UserProfileMapper {

    public UserProfileDto toDto(Profile profile) {
        if (profile == null) {
            return null;
        }
        UserProfileDto dto = new UserProfileDto();
        dto.setTag(profile.getTag());
        dto.setEmail(profile.getEmail());
        dto.setName(profile.getName());
        dto.setSurname(profile.getSurname());
        dto.setPhoneNumber(profile.getPhoneNumber());
        dto.setWeight(profile.getWeight());
        dto.setHeight(profile.getHeight());
        dto.setHobby(profile.getHobby());
        dto.setProfession(profile.getProfession());
        dto.setEarnings(profile.getEarnings());
        dto.setAge(profile.getAge());
        dto.setGender(profile.getGender());

        dto.setAboutMe(profile.getAboutMe());
        dto.setCity(profile.getCity());
        dto.setCountry(profile.getCountry());
        dto.setPersonalityExtraversion(profile.getPersonalityExtraversion());
        dto.setPersonalityOpenness(profile.getPersonalityOpenness());
        dto.setPersonalityConscientiousness(profile.getPersonalityConscientiousness());
        dto.setLifeValueFamily(profile.getLifeValueFamily());
        dto.setLifeValueCareer(profile.getLifeValueCareer());
        dto.setActivityLevel(profile.getActivityLevel());
        return dto;
    }

    public Profile toEntity(UserProfileDto dto) {
        if (dto == null) {
            return null;
        }
        Profile profile = new Profile();
        profile.setTag(dto.getTag());
        profile.setEmail(dto.getEmail());
        profile.setName(dto.getName());
        profile.setSurname(dto.getSurname());
        profile.setPhoneNumber(dto.getPhoneNumber());
        profile.setWeight(dto.getWeight());
        profile.setHeight(dto.getHeight());
        profile.setHobby(dto.getHobby());
        profile.setProfession(dto.getProfession());
        profile.setEarnings(dto.getEarnings());
        profile.setAge(dto.getAge());
        profile.setGender(dto.getGender());
        profile.setAboutMe(dto.getAboutMe());
        profile.setCity(dto.getCity());
        profile.setCountry(dto.getCountry());
        profile.setPersonalityExtraversion(dto.getPersonalityExtraversion());
        profile.setPersonalityOpenness(dto.getPersonalityOpenness());
        profile.setPersonalityConscientiousness(dto.getPersonalityConscientiousness());
        profile.setLifeValueFamily(dto.getLifeValueFamily());
        profile.setLifeValueCareer(dto.getLifeValueCareer());
        profile.setActivityLevel(dto.getActivityLevel());
        return profile;
    }

    public KafkaDto toKafkaDto(Profile profile) {
        if (profile == null) {
            throw new IllegalArgumentException("Profile cannot be null");
        }

        KafkaDto dto = new KafkaDto();
        dto.setUserTag(profile.getTag());
        dto.setHeight(profile.getHeight());
        dto.setAge(profile.getAge() != null ? profile.getAge().longValue() : null);
        dto.setWeight(profile.getWeight());
        dto.setEarnings(profile.getEarnings());
        dto.setBeauty(profile.getBeauty());
        dto.setPersonalityExtraversion(profile.getPersonalityExtraversion());
        dto.setPersonalityOpenness(profile.getPersonalityOpenness());
        dto.setPersonalityConscientiousness(profile.getPersonalityConscientiousness());
        dto.setLifeValueFamily(profile.getLifeValueFamily());
        dto.setLifeValueCareer(profile.getLifeValueCareer());
        dto.setActivityLevel(profile.getActivityLevel());
        dto.setGender(profile.getGender());
        return dto;
    }

    public Location toLocation(UserProfileDto dto) {
        if (dto == null) {
            return null;
        }
        Location location = new Location();
        location.setCity(dto.getCity());
        location.setCountry(dto.getCountry());

        if (dto.getLocation() != null) {
            location.setLocation(dto.getLocation());
        }

        return location;
    }
}