package com.TAstanov.MyChat_Swipe.repository;

import com.TAstanov.MyChat_Swipe.domain.Like;
import com.TAstanov.MyChat_Swipe.domain.LikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface LikesRepository extends JpaRepository<Like, LikeId> {

    @Transactional
    @Modifying
    @Query(value = """
           DELETE  FROM likes WHERE expiration_date > :exritation""", nativeQuery = true)
    void deleteExpiredLikes(@Param("expiration") LocalDateTime expirationDate);

}
