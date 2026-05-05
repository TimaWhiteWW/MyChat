package com.TAstanov.MyChat_UserMicroservice.repository;

import com.TAstanov.MyChat_UserMicroservice.domain.pictures.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfilePictureRepository extends JpaRepository<Avatar, Long> {



}
