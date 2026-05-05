package com.TAstanov.MyChat_Recomendation.repository;


import com.TAstanov.MyChat_Recomendation.domain.vector.VectorizedUserSql;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VectorizedUserRepositorySql extends JpaRepository<VectorizedUserSql, String> {

    VectorizedUserSql getByUserTag(String userTag);

}
