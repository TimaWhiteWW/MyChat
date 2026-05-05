package com.TAstanov.MyChat_UserMicroservice.repository;

import com.TAstanov.MyChat_UserMicroservice.domain.profiles.Profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Profile, Long> {

}
