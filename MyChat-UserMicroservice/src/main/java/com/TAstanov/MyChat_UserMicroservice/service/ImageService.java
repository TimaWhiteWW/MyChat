package com.TAstanov.MyChat_UserMicroservice.service;

import com.TAstanov.MyChat_UserMicroservice.domain.pictures.Avatar;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    Avatar uploadImage(MultipartFile multipartFile, String userTag);

}
