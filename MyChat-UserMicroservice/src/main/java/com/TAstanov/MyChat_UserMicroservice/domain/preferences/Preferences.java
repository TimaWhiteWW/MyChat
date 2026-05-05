package com.TAstanov.MyChat_UserMicroservice.domain.preferences;

import com.TAstanov.MyChat_UserMicroservice.domain.profiles.HOBBY;
import com.TAstanov.MyChat_UserMicroservice.domain.profiles.PROFESSION;
import com.TAstanov.MyChat_UserMicroservice.repository.converter.HobbyConverter;
import com.TAstanov.MyChat_UserMicroservice.repository.converter.ProfessionsConverter;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "user_preferences")
public class Preferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_tag", nullable = false)
    private String userTag;

    @Convert(converter = HobbyConverter.class)
    @Column(name = "preferences_hobby")
    private List<HOBBY> preferencesHobby;

    @Convert(converter = ProfessionsConverter.class)
    @Column(name = "preferences_professions")
    private List<PROFESSION> preferencesProfessions;

    @Column(name = "min_earnings")
    private Long minEarnings;

    @Column(name = "min_bmi")
    private Double minBMI;

    @Column(name = "max_bmi")
    private Double maxBMI;

    @Column(name = "min_height")
    private Double minHeight;

    @Column(name = "max_height")
    private Double maxHeight;

    @Column(name = "min_age")
    private Double minAge;

    @Column(name = "max_age")
    private Double maxAge;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "distance")
    private Integer distance;
}
