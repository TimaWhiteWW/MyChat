package com.TAstanov.MyChat_UserMicroservice.service.Impl;

import com.TAstanov.MyChat_UserMicroservice.domain.profiles.Profile;
import com.TAstanov.MyChat_UserMicroservice.service.KafkaService;
import com.TAstanov.MyChat_UserMicroservice.web.dto.KafkaDto;
import com.TAstanov.MyChat_UserMicroservice.web.mapper.UserProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Service
@RequiredArgsConstructor
public class KafkaServiceImpl implements KafkaService {

    private final KafkaSender<String, Object> kafkaSender;
    private final UserProfileMapper userProfileMapper;

    @Override
    public void send(Profile profile) {
        KafkaDto dto = userProfileMapper.toKafkaDto(profile);
        dto.setBeauty(0);
        kafkaSender.send(
                Mono.just(
                    SenderRecord.create(
                            "kb-tree",
                            0,
                            System.currentTimeMillis(),
                            String.valueOf(dto.hashCode()),
                            dto,
                            null
                    )
                )
        ).subscribe();
    }
}
