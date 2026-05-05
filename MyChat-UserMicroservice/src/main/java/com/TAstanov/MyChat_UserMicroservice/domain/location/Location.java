package com.TAstanov.MyChat_UserMicroservice.domain.location;

import jakarta.persistence.*;
import lombok.Data;
import org.locationtech.jts.geom.Point;


@Data
@Entity
@Table(name = "user_locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userTag;

    private String city;
    private String country;
    private Point location;
}
