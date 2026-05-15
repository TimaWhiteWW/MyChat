package com.TAstanov.MyChat_Auth.web.controller;

import com.TAstanov.MyChat_Auth.domain.jwtResponse.JwtResponse;
import com.TAstanov.MyChat_Auth.domain.user.User;
import com.TAstanov.MyChat_Auth.service.AuthService;
import com.TAstanov.MyChat_Auth.service.JwtTokenService;
import com.TAstanov.MyChat_Auth.web.dto.LogoutRequest;
import com.TAstanov.MyChat_Auth.web.dto.RegisterRequest;
import com.TAstanov.MyChat_Auth.web.dto.VerifyEmailRequest;
import com.TAstanov.MyChat_Auth.web.dto.mapper.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenService jwtTokenService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public boolean register(@Valid @RequestBody RegisterRequest registerRequest, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new RuntimeException();
        }
        User user = userMapper.toEntity(registerRequest);
        return authService.register(user);
    }

    @PostMapping("/verify")
    public boolean verify(@Valid @RequestBody VerifyEmailRequest verifyEmailRequest, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new RuntimeException();
        }
        return authService.verifyEmail(verifyEmailRequest.getEmail(), verifyEmailRequest.getCode());
    }

    @GetMapping("/login")
    public JwtResponse login(@RequestParam String email, @RequestParam String password){
        return jwtTokenService.login(email, password);
    }

    @DeleteMapping("/logout")
    public void logout(@RequestBody LogoutRequest logoutRequest){
        jwtTokenService.logout(logoutRequest);
    }

    @PostMapping("/refresh")
    public JwtResponse refresh(@RequestBody LogoutRequest refreshRequest){
        JwtResponse newTokenPair = jwtTokenService.refresh(refreshRequest.getRefreshToken());
        logout(refreshRequest);
        return newTokenPair;
    }


}
