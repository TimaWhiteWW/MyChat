package com.TAstanov.MyChat_Auth.repository;

import com.TAstanov.MyChat_Auth.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    @Query(value = "SELECT * FROM user_auth WHERE email = :email AND password = :password", nativeQuery = true)
    Optional<User> checkPassword(@Param("email") String email, @Param("password") String password);

}
