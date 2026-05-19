package com.TAstanov.MyChat_UserMicroservice.repository;

import com.TAstanov.MyChat_UserMicroservice.domain.profiles.Profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByTag(String tag);

    Optional<Profile> findByEmail(String email);
}
