package com.TAstanov.MyChat_Recomendation.repository;

import com.TAstanov.MyChat_Recomendation.domain.user.UserTemporalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TemporaryRepository extends JpaRepository<UserTemporalData, String> {

    @Query(value = """
            SELECT user_tag FROM user_temporal_data""", nativeQuery = true)
    List<String> getAllTags();

    @Transactional
    @Modifying
    @Query(value = "TRUNCATE TABLE user_temporal_data", nativeQuery = true)
    void deleteAll();

    @Query(value = """
            SELECT * FROM user_temporal_data
            ORDER BY id
            LIMIT :limit OFFSET :offset""", nativeQuery = true)
    List<UserTemporalData> findAllWithOffsetAndLimit(
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    UserTemporalData findByUserTag(String userTag);
}
