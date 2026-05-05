package com.TAstanov.MyChat_Auth.domain.jwtResponse;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table(name="jwt_refresh_token")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class JwtRefreshToken {
    @Id
    private UUID id;
    private String refreshToken;
    private LocalDateTime expirationDate;
}