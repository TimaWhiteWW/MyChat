package com.TAstanov.MyChat_UserMicroservice.service.Impl;

import com.TAstanov.MyChat_UserMicroservice.domain.location.Location;
import com.TAstanov.MyChat_UserMicroservice.domain.profiles.Profile;
import com.TAstanov.MyChat_UserMicroservice.repository.LocationRepository;
import com.TAstanov.MyChat_UserMicroservice.repository.UserRepository;
import com.TAstanov.MyChat_UserMicroservice.service.ImageService;
import com.TAstanov.MyChat_UserMicroservice.service.KafkaService;
import com.TAstanov.MyChat_UserMicroservice.service.UserService;
import com.TAstanov.MyChat_UserMicroservice.web.dto.UserProfileDto;
import com.TAstanov.MyChat_UserMicroservice.web.mapper.UserProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final ImageService imageService;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final KafkaService kafkaService;
    private final UserProfileMapper userProfileMapper;

    @Override
    @Transactional
    public void create(UserProfileDto userProfileDto) {
        Profile profile = saveProfile(userProfileDto);
        saveLocation(userProfileDto, profile);
        uploadImages(userProfileDto.getPictures(), profile.getTag());
        kafkaService.send(profile);
    }

    private Profile saveProfile(UserProfileDto userProfileDto) {
        Profile profile = userProfileMapper.toEntity(userProfileDto);
        return userRepository.save(profile); // Сохраняем и возвращаем профиль
    }

    private void saveLocation(UserProfileDto userProfileDto, Profile profile) {
        Location location = userProfileMapper.toLocation(userProfileDto);
        location.setUserTag(profile.getTag());  // Используем tag вместо id
        locationRepository.saveUserLocation(profile.getTag(), location.getCity(), location.getCountry(), location.getLocation().toString());
    }

    private void uploadImages(List<MultipartFile> multipartFiles, String userTag) {  // Используем tag вместо id
        if (multipartFiles == null || multipartFiles.isEmpty()) {
            return;
        }
        multipartFiles.forEach(file -> imageService.uploadImage(file, userTag)); // Используем tag
    }
}
