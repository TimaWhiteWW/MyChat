package com.TAstanov.MyChat_Notification.domain;

import lombok.Data;

@Data
public class DemoUser {
    private String id;
    private String name;
    private String email;
    private String password;
    private Boolean emailVerified;
    private Integer age;
    private String city;
    private String gender;
    private String interests;
    private String bio;
    private String photo;
}
