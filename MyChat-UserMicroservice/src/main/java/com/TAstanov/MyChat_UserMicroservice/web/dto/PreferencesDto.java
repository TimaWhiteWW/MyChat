package com.TAstanov.MyChat_UserMicroservice.web.dto;

import com.TAstanov.MyChat_UserMicroservice.domain.profiles.HOBBY;
import com.TAstanov.MyChat_UserMicroservice.domain.profiles.PROFESSION;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class PreferencesDto {

    @NotNull(message = "User tag cannot be null")
    @NotEmpty(message = "User tag cannot be empty")
    private String userTag;

    @NotEmpty(message = "Preferences for hobbies cannot be empty")
    private List<HOBBY> preferencesHobby;

    @NotEmpty(message = "Preferences for professions cannot be empty")
    private List<PROFESSION> preferencesProfessions;

    @Min(value = 0, message = "Min earnings should be at least 0")
    private Long minEarnings;

    @DecimalMin(value = "0.0", inclusive = true, message = "Min BMI should be greater than or equal to 0")
    @DecimalMax(value = "100.0", inclusive = true, message = "Min BMI should be less than or equal to 100")
    private Double minBMI;

    @DecimalMin(value = "0.0", inclusive = true, message = "Max BMI should be greater than or equal to 0")
    @DecimalMax(value = "100.0", inclusive = true, message = "Max BMI should be less than or equal to 100")
    private Double maxBMI;

    @DecimalMin(value = "0.0", inclusive = true, message = "Min height should be greater than or equal to 0")
    @DecimalMax(value = "3.0", inclusive = true, message = "Min height should be less than or equal to 3")
    private Double minHeight;

    @DecimalMin(value = "0.0", inclusive = true, message = "Max height should be greater than or equal to 0")
    @DecimalMax(value = "3.0", inclusive = true, message = "Max height should be less than or equal to 3")
    private Double maxHeight;

    @Min(value = 0, message = "Min age should be greater than or equal to 0")
    @Max(value = 150, message = "Max age should be less than or equal to 150")
    private Double minAge;

    @Min(value = 0, message = "Min age should be greater than or equal to 0")
    @Max(value = 150, message = "Max age should be less than or equal to 150")
    private Double maxAge;

    @Min(value = 0, message = "Distance should be greater than or equal to 0")
    @Max(value = 40_000, message = "We work only on earth :)")
    private Integer distance;
}

