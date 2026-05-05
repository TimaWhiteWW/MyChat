package com.TAstanov.MyChat_Recomendation.domain.user;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "user_temporal_data")
public class UserTemporalData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userTag;
    private Integer temporaryTable;
    private Integer offsetUsers;

}

