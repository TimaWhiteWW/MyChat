package com.TAstanov.MyChat_LoadedTest.entity;

import lombok.Data;

import java.util.List;

@Data
public class Profile {

    private Long id;

    private String tag;

    private String name;

    private String surname;

    private String email;

    private String phoneNumber;

    private Integer weight;

    private Double height;

    private List<HOBBY> hobby;

    private List<PROFESSION> profession;

    private Long earnings;

    private Integer age;

    private GENDER gender;

    private String aboutMe;

    private String city;

    private String country;

    private Double personalityExtraversion; // Экстраверсия

    private Double personalityOpenness;     // Открытость новому опыту

    private Double personalityConscientiousness; // Добросовестность

    private Double lifeValueFamily; // Важность семьи

    private Double lifeValueCareer; // Важность карьеры

    private Double activityLevel; // Физическая/социальная активность

    private Integer beauty;

    private List<String> pictures;

    public enum GENDER {
        MALE, FEMALE
    }

}
