package com.TAstanov.MyChat_Recomendation.service.Impl.storage;

import com.TAstanov.MyChat_Recomendation.domain.vector.VectorizedUser;
import com.TAstanov.MyChat_Recomendation.domain.vector.VectorizedUserMapper;
import com.TAstanov.MyChat_Recomendation.domain.vector.VectorizedUserSql;
import com.TAstanov.MyChat_Recomendation.repository.VectorizedUserRepositorySql;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VectorizedUserService {

    private final VectorizedUserRepositorySql vectorizedUserRepositorySql;
    private final VectorizedUserMapper vectorizedUserMapper;

    public VectorizedUser getVectorizedUserByTag(String userTag) {
        VectorizedUserSql vectorizedUserSql = vectorizedUserRepositorySql.getByUserTag(userTag);
        return vectorizedUserMapper.toElasticsearchEntity(vectorizedUserSql);
    }
}
