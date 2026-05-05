package com.TAstanov.MyChat_Swipe.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Data;
import org.apache.kafka.common.protocol.types.Field;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "\"likes\"")
@IdClass(LikeId.class)
public class Like {

    @Id
    private String userTag;
    @Id
    private String likedUserTag;

    private LocalDateTime expirationDate;
}
