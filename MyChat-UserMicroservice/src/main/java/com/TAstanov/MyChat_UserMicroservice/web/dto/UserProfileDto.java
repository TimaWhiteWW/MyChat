package com.TAstanov.MyChat_UserMicroservice.web.dto;

import com.TAstanov.MyChat_UserMicroservice.domain.profiles.HOBBY;
import com.TAstanov.MyChat_UserMicroservice.domain.profiles.PROFESSION;
import com.TAstanov.MyChat_UserMicroservice.domain.profiles.Profile;
import com.TAstanov.MyChat_UserMicroservice.web.deserializer.PointDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.locationtech.jts.geom.Point;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UserProfileDto {

    @NotBlank(message = "Tag cannot be blank")
    @Size(min = 3, max = 20, message = "Tag must be between 3 and 20 characters")
    private String tag;                        // Имя пользователя

    @NotNull(message = "Email cannot be null")
    @Email(message = "Invalid email format")
    private String email;                      // Электронная почта

    @NotNull
    private String name;

    @NotNull
    private String surname;

    @Pattern(regexp = "^\\+?[0-9]*$", message = "Phone number must contain only digits and optional leading plus sign")
    private String phoneNumber;                // Номер телефона (необязательный)

    @NotNull
    private Integer weight;

    @NotNull
    private Double height;

    private List<HOBBY> hobby;

    @NotNull
    private List<PROFESSION> profession;

    private Long earnings;

    @NotNull(message = "Age cannot be null")
    private Integer age;

    @NotNull(message = "Gender cannot be null")
    private Profile.GENDER gender;         // Пол пользователя (например, MALE, FEMALE, OTHER)

    @Size(max = 500, message = "About me section must be 500 characters or less")
    private String aboutMe;                    // Описание профиля

    @NotBlank(message = "City cannot be blank")
    private String city;                       // Город проживания

    @NotBlank(message = "Country cannot be blank")
    private String country;                    // Страна

    @JsonDeserialize(using = PointDeserializer.class)
    private Point location;

    //@NotNull()
    List<MultipartFile> pictures;

    @NotNull
    private Double personalityExtraversion;

    @NotNull
    private Double personalityOpenness;

    @NotNull
    private Double personalityConscientiousness;

    @NotNull
    private Double lifeValueFamily;

    @NotNull
    private Double lifeValueCareer;

    @NotNull
    private Double activityLevel;

}
