package com.TAstanov.MyChat_Recomendation.domain.vector;

import org.springframework.stereotype.Component;

@Component
public class VectorizedUserMapper {

    /**
     * Преобразует VectorizedUserSql в VectorizedUserEl.
     *
     * @param sqlEntity Сущность из SQL.
     * @return Сущность для Elasticsearch.
     */
    public VectorizedUser toElasticsearchEntity(VectorizedUserSql sqlEntity) {
        VectorizedUser elasticEntity = new VectorizedUser();
        elasticEntity.setUserTag(sqlEntity.getUserTag());
        elasticEntity.setGender(sqlEntity.getGender());
        elasticEntity.setVector(sqlEntity.getVectorArray());
        return elasticEntity;
    }

    /**
     * Преобразует VectorizedUserEl в VectorizedUserSql.
     *
     * @param elasticEntity Сущность из Elasticsearch.
     * @return Сущность для SQL.
     */
    public VectorizedUserSql toSqlEntity(VectorizedUser elasticEntity) {
        VectorizedUserSql sqlEntity = new VectorizedUserSql();
        sqlEntity.setUserTag(elasticEntity.getUserTag());
        sqlEntity.setGender(elasticEntity.getGender());
        sqlEntity.setVectorArray(elasticEntity.getVector());
        return sqlEntity;
    }
}
