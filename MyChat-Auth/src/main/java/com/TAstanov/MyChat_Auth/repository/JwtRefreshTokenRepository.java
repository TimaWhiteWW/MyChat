package com.TAstanov.MyChat_Auth.repository;

import com.TAstanov.MyChat_Auth.domain.jwtResponse.JwtRefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface JwtRefreshTokenRepository extends JpaRepository<JwtRefreshToken, UUID> {

    @Modifying
    @Query(value = """
           SELECT * FROM jwt_refresh_token
           WHERE expiration_date < :now""", nativeQuery = true)
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM jwt_refresh_token WHERE refresh_token = :refreshToken", nativeQuery = true)
    void deleteJwtRefreshTokenByRefreshToken(String refreshToken);

}
