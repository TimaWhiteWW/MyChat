package com.TAstanov.MyChat_LoadedTest.entity;

import lombok.Data;

@Data
public class RegisterRequest {

    private String name;

    private String email;

    private String tag;

    private String password;

    private String passwordConfirmation;

    public static RegisterRequest generateSingleRegisterRequest() {
        long timestamp = System.currentTimeMillis();

        RegisterRequest request = new RegisterRequest();
        request.setName("Test User " + timestamp);
        request.setEmail("user" + timestamp + "@example.com");
        request.setTag("tag" + timestamp);
        request.setPassword("Password" + timestamp + "!");
        request.setPasswordConfirmation("Password" + timestamp + "!");

        return request;
    }

}