package com.TAstanov.MyChat_Recomendation.service.Impl.storage;

import com.TAstanov.MyChat_Recomendation.domain.user.GENDER;
import com.TAstanov.MyChat_Recomendation.domain.user.UserTemporalData;
import com.TAstanov.MyChat_Recomendation.domain.vector.VectorizedUser;
import com.TAstanov.MyChat_Recomendation.repository.VectorizedUserRepository;
import com.TAstanov.MyChat_Recomendation.service.ElasticsearchService;
import com.TAstanov.MyChat_Recomendation.service.props.ElasticsearchProps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchIndexService implements ElasticsearchService {

    private final VectorizedUserRepository vectorizedUserRepository;
    private final ElasticsearchProps elasticsearchProps;

    @Override
    public void saveUser(VectorizedUser user, UserTemporalData temporalData) {
        vectorizedUserRepository.save(user, temporalData);
        log.info("Пользователь: {}успешно coхранен в elasticsearch", user.getUserTag());
    }

    @Override
    public Optional<VectorizedUser> findByUserTag(String userTag) {
        return Optional.empty();
    }

    @Override
    public List<VectorizedUser> findByGender(GENDER gender) {
        // TODO: Реализовать поиск пользователей по полу
        return List.of();
    }

    @Override
    public List<VectorizedUser> findByVector(double[] vector) {
        // TODO: Реализовать поиск пользователей по вектору
        return List.of();
    }

    @Override
    public void deleteUser(String userTag) {
        // TODO: Реализовать удаление пользователя
    }

    @Override
    public void dropAll() {
        for(int i = elasticsearchProps.getMinShardNumber(); i <= elasticsearchProps.getMaxShardNumber(); i++){
            vectorizedUserRepository.truncateIndex(GENDER.MALE, i);
        }
        for(int i = elasticsearchProps.getMinShardNumber(); i <= elasticsearchProps.getMaxShardNumber(); i++){
            vectorizedUserRepository.truncateIndex(GENDER.FEMALE, i);
        }
    }

    @Override
    public void saveAllUsers(List<VectorizedUser> users, List<UserTemporalData> temporalDataList) {
        vectorizedUserRepository.saveAll(users, temporalDataList);
        log.info("Сохранено {} пользователей в Elasticsearch", users.size());
    }

    @Override
    public List<VectorizedUser> getSimilarUsers(VectorizedUser vectorizedUser, UserTemporalData temporalData) {
         List<VectorizedUser> vectorizedUsers = vectorizedUserRepository.findSimilarUsers(vectorizedUser, temporalData, 100);

        return vectorizedUsers;
    }


}
