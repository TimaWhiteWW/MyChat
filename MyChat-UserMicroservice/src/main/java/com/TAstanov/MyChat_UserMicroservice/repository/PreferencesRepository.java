package com.TAstanov.MyChat_UserMicroservice.repository;

import com.TAstanov.MyChat_UserMicroservice.domain.preferences.Preferences;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PreferencesRepository extends JpaRepository<Preferences, Long> {
}
