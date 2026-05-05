package com.TAstanov.MyChat_Auth.service;

import com.TAstanov.MyChat_Auth.domain.jwtResponse.JwtResponse;
import com.TAstanov.MyChat_Auth.web.dto.LogoutRequest;

public interface JwtTokenService {

    JwtResponse login(String email, String password);

    void logout(LogoutRequest logoutRequest);

    JwtResponse refresh(String refreshToken);

}
