package com.TAstanov.MyChat_Recomendation.domain.user;

import com.TAstanov.MyChat_Recomendation.repository.converter.HobbyConverter;
import com.TAstanov.MyChat_Recomendation.repository.converter.ProfessionsConverter;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "user_profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tag;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(nullable = false, unique = true)
    private String email;

    private String phoneNumber;

    @Column(nullable = false)
    private Integer weight;

    @Column(nullable = false)
    private Double height;

    @Enumerated(EnumType.STRING)
    @Convert(converter = HobbyConverter.class)
    private List<HOBBY> hobby;

    @Enumerated(EnumType.STRING)
    @Convert(converter = ProfessionsConverter.class)
    private List<PROFESSION> profession;

    private Long earnings;

    @Column(nullable = false)
    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GENDER gender;

    @Column(length = 500)
    private String aboutMe;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String country;

    // Лёгкий опрос по личности: шкалы 0-10
    @Column(name = "personality_extraversion")
    private Double personalityExtraversion; // Экстраверсия

    @Column(name = "personality_openness")
    private Double personalityOpenness;     // Открытость новому опыту

    @Column(name = "personality_conscientiousness")
    private Double personalityConscientiousness; // Добросовестность

    // Ключевые жизненные ценности: семейные и карьерные приоритеты (0-10)
    @Column(name = "life_value_family")
    private Double lifeValueFamily; // Важность семьи

    @Column(name = "life_value_career")
    private Double lifeValueCareer; // Важность карьеры

    // Простой показатель лайфстайла: уровень активности (0-10)
    @Column(name = "activity_level")
    private Double activityLevel; // Физическая/социальная активность

    @Column(name="beauty")
    private Integer beauty;

    @Transient
    private List<String> pictures; // Хранение URL-ов или имен файлов изображений (можно переделать в отдельную сущность)

    public enum GENDER {
        MALE, FEMALE
    }

}
