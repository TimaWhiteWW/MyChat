package com.TAstanov.MyChat_Recomendation.service;

import com.TAstanov.MyChat_Recomendation.domain.user.GENDER;
import com.TAstanov.MyChat_Recomendation.domain.user.UserTemporalData;
import com.TAstanov.MyChat_Recomendation.domain.vector.VectorizedUser;

import java.util.List;
import java.util.Optional;

public interface ElasticsearchService {

    /**
     * Сохранение пользователя в Elasticsearch
     */
    void saveUser(VectorizedUser user, UserTemporalData temporalData);

    /** Поиск пользователя по userTag */
    Optional<VectorizedUser> findByUserTag(String userTag);

    /** Поиск всех пользователей по полу */
    List<VectorizedUser> findByGender(GENDER gender);

    /** Поиск пользователей по вектору */
    List<VectorizedUser> findByVector(double[] vector);

    /** Удаление пользователя */
    void deleteUser(String userTag);

    void dropAll();


    void saveAllUsers(List<VectorizedUser> users, List<UserTemporalData> temporalDataList);

    List<VectorizedUser> getSimilarUsers(VectorizedUser vectorizedUser, UserTemporalData userTemporalData);
}
