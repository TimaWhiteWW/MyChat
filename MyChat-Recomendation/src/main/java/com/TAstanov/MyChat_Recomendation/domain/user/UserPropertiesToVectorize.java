package com.TAstanov.MyChat_Recomendation.domain.user;

import lombok.Data;

@Data
public class UserPropertiesToVectorize {

    private String userTag;

    private Double height;

    private Integer age;

    private Integer weight;

    private GENDER gender;

    private Long earnings;

    private Integer beauty;

    private Double personalityExtraversion; // Экстраверсия

    private Double personalityOpenness;     // Открытость новому опыту

    private Double personalityConscientiousness; // Добросовестность

    private Double lifeValueFamily; // Важность семьи

    private Double lifeValueCareer; // Важность карьеры

    private Double activityLevel; // Физическая/социальная активность
}
