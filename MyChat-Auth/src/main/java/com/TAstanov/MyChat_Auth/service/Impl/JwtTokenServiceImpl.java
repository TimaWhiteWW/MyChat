package com.TAstanov.MyChat_Auth.service.Impl;

import com.TAstanov.MyChat_Auth.domain.exception.PasswordMismatch;
import com.TAstanov.MyChat_Auth.domain.exception.UserIsNotPresent;
import com.TAstanov.MyChat_Auth.domain.jwtResponse.JwtAccessToken;
import com.TAstanov.MyChat_Auth.domain.jwtResponse.JwtRefreshToken;
import com.TAstanov.MyChat_Auth.domain.jwtResponse.JwtResponse;
import com.TAstanov.MyChat_Auth.domain.user.User;
import com.TAstanov.MyChat_Auth.repository.AuthRepository;
import com.TAstanov.MyChat_Auth.repository.JwtAccessTokenRepository;
import com.TAstanov.MyChat_Auth.repository.JwtRefreshTokenRepository;
import com.TAstanov.MyChat_Auth.service.JwtTokenService;
import com.TAstanov.MyChat_Auth.service.props.JwtProps;
import com.TAstanov.MyChat_Auth.web.dto.LogoutRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtTokenServiceImpl implements JwtTokenService {

    private final JwtAccessTokenRepository jwtAccessTokenRepository;
    private final JwtRefreshTokenRepository jwtRefreshTokenRepository;
    private final AuthRepository authRepository;
    private final JwtProps jwtProps;
    private final PasswordEncoder passwordEncoder;
    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtProps.getSecretKey().getBytes());
    }

    @Override
    public JwtResponse login(String email, String password) {
        validateUserCredentials(email, password);
        return createJwtResponse(email);
    }

    @Override
    @Transactional
    public void logout(LogoutRequest logoutRequest) {
        jwtAccessTokenRepository.deleteJwtAccessTokenByAccessToken(logoutRequest.getAccessToken());
        jwtRefreshTokenRepository.deleteJwtRefreshTokenByRefreshToken(logoutRequest.getRefreshToken());
    }

    @Override
    public JwtResponse refresh(String refreshToken) {
        if (isValid(refreshToken)) {
            String email = parseEmailFromToken(refreshToken);
            return createJwtResponse(email);
        }
        throw new JwtException("Invalid refresh token");
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void deleteExpiredAccessToken() {
        jwtAccessTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void deleteExpiredRefreshToken() {
        jwtRefreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }

    private void validateUserCredentials(String email, String password) {
        User user = authRepository.findByEmail(email)
                .orElseThrow(() -> new UserIsNotPresent("User not found"));
        if (!Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new IllegalArgumentException("Email is not verified");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordMismatch("Invalid password");
        }
    }

    private JwtResponse createJwtResponse(String email) {
        String accessToken = createAccessToken(email);
        String refreshToken = createRefreshToken(email);

        saveTokens(accessToken, refreshToken);

        return new JwtResponse(accessToken, refreshToken);
    }

    private void saveTokens(String accessToken, String refreshToken) {
        JwtAccessToken jwtAccessToken = new JwtAccessToken(UUID.randomUUID(), accessToken, LocalDateTime.now().plus(Duration.ofSeconds(jwtProps.getAccessLiveTime())));
        JwtRefreshToken jwtRefreshToken = new JwtRefreshToken(UUID.randomUUID(), refreshToken, LocalDateTime.now().plus(Duration.ofSeconds(jwtProps.getRefreshLiveTime())));

        jwtAccessTokenRepository.save(jwtAccessToken);
        jwtRefreshTokenRepository.save(jwtRefreshToken);
    }

    private String createAccessToken(String email) {
        return createToken(email, jwtProps.getAccessLiveTime());
    }

    private String createRefreshToken(String email) {
        return createToken(email, jwtProps.getRefreshLiveTime());
    }

    private String createToken(String email, long validityInSeconds) {
        Claims claims = Jwts.claims().subject(email).build();
        Instant validity = Instant.now().plus(Duration.ofSeconds(validityInSeconds));

        return Jwts.builder()
                .claims(claims)
                .expiration(Date.from(validity))
                .signWith(key)
                .compact();
    }

    private boolean isValid(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token);

            return !claims.getPayload().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private String parseEmailFromToken(String token) {
        Jws<Claims> claims = Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token);

        return claims.getPayload().getSubject();
    }
}
