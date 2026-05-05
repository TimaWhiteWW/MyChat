package com.TAstanov.MyChat_Recomendation.service.Impl.logic;

import com.TAstanov.MyChat_Recomendation.domain.user.Profile;
import com.TAstanov.MyChat_Recomendation.domain.user.UserTemporalData;
import com.TAstanov.MyChat_Recomendation.domain.vector.VectorizedUser;
import com.TAstanov.MyChat_Recomendation.repository.TemporaryRepository;
import com.TAstanov.MyChat_Recomendation.repository.UserRepository;
import com.TAstanov.MyChat_Recomendation.service.ElasticsearchService;
import com.TAstanov.MyChat_Recomendation.service.Impl.storage.CacheService;
import com.TAstanov.MyChat_Recomendation.service.Impl.storage.VectorizedUserService;
import com.TAstanov.MyChat_Recomendation.service.SelectPartnerStackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SelectPartnerStack implements SelectPartnerStackService {

    private final ElasticsearchService elasticsearchService;
    private final VectorizedUserService vectorizedUserService;
    private final TemporaryRepository temporaryRepository;
    private final UserRepository userRepository;
    private final CacheService cacheService;

    @Override
    public List<Profile> formPartnerStack(String requestUserTag) {

        List<Profile> profiles = cacheService.getUserPartnerStack(requestUserTag, 10);

        if(profiles != null){
            return profiles;
        }

        VectorizedUser vectorizedUser = vectorizedUserService.getVectorizedUserByTag(requestUserTag);
        UserTemporalData temporalData = temporaryRepository.findByUserTag(requestUserTag);

        List<VectorizedUser> similarUsers = elasticsearchService.getSimilarUsers(vectorizedUser, temporalData);

        profiles = userRepository.getProfilesByTags(similarUsers.stream().map(VectorizedUser::getUserTag).toList());
        cacheService.saveUserPartnerStack(profiles, requestUserTag);

        return profiles.subList(0, 20);
    }
}
