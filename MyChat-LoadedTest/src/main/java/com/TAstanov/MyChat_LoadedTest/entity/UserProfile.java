package com.TAstanov.MyChat_LoadedTest.entity;

import com.TAstanov.MyChat_LoadedTest.controller.PointSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Data
public class UserProfile {

    private String tag;                        // Имя пользователя

    private String email;                      // Электронная почта

    private String name;

    private String surname;

    private String phoneNumber;                // Номер телефона (необязательный)

    private Integer weight;

    private Double height;

    private List<HOBBY> hobby;

    private List<PROFESSION> profession;

    private Long earnings;

    private Integer age;

    private GENDER gender;         // Пол пользователя (например, MALE, FEMALE, OTHER)

    private String aboutMe;                    // Описание профиля

    private String city;                       // Город проживания

    private String country;                    // Страна

    @JsonSerialize(using = PointSerializer.class)
    private Point location;

    List<MultipartFile> pictures;

    private Double personalityExtraversion;

    private Double personalityOpenness;

    private Double personalityConscientiousness;

    private Double lifeValueFamily;

    private Double lifeValueCareer;

    private Double activityLevel;

    public static UserProfile generateSingleUserProfile() {
        Random random = new Random();
        long ts = System.currentTimeMillis();
        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        UserProfile p = new UserProfile();
        p.setTag("tag" + ts + random.nextInt(1, 1000000));
        p.setEmail("user" + ts + random.nextInt() + "@example.com");
        p.setName("Name" + ts);
        p.setSurname("Surname" + ts);
        p.setPhoneNumber("+" + (1000000000L + ts % 1000000000));
        p.setWeight(50 + (int)(ts % 50));                           // от 50 до 99
        p.setHeight(1.5 + (ts % 50) / 100.0);                       // от 1.50 до 1.99

        // выбрать по одному случайному enum
        HOBBY[] hobbies = HOBBY.values();
        p.setHobby(List.of(hobbies[(int)(ts % hobbies.length)]));
        PROFESSION[] profs = PROFESSION.values();
        p.setProfession(List.of(profs[(int)((ts+1) % profs.length)]));

        p.setEarnings(1000L + ts % 100_000);                        // от 1000 до 100999
        p.setAge(18 + (int)(ts % 50));                              // от 18 до 67
        p.setGender((ts % 2 == 0) ? GENDER.MALE : GENDER.FEMALE);
        p.setAboutMe("About me text " + ts);
        p.setCity("City" + ts);
        p.setCountry("Country" + ts);


        Point pt = new GeometryFactory().createPoint(new Coordinate(0, 0));
        p.setLocation(pt);

        // пустой список картинок
        p.setPictures(Collections.<MultipartFile>emptyList());

        // в диапазоне 0–10
        double base = (ts % 1000) / 100.0;
        p.setPersonalityExtraversion(base);
        p.setPersonalityOpenness(base + 0.1);
        p.setPersonalityConscientiousness(base + 0.2);
        p.setLifeValueFamily(base + 0.3);
        p.setLifeValueCareer(base + 0.4);
        p.setActivityLevel(base + 0.5);

        return p;
    }

    public enum GENDER {
        MALE, FEMALE
    }

    public byte[] getPictureBytes(){
        return new byte[600];
    }
}
