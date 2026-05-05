package com.TAstanov.MyChat_Swipe.service.Impl;

import com.TAstanov.MyChat_Swipe.service.KafkaService;
import com.TAstanov.MyChat_Swipe.service.props.KafkaProps;
import com.google.gson.Gson;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Service
@RequiredArgsConstructor
public class KafkaServiceImpl implements KafkaService {

    private final KafkaSender<String, String> kafkaSender;
    private final KafkaProps kafkaProps;
    private final Gson gson = new Gson();

    @Override
    public Mono<Void> send(String userTag, String likedUserTag) {
        LikeKafkaDto likeKafkaDto = new LikeKafkaDto();
        likeKafkaDto.setValue1(userTag);
        likeKafkaDto.setValue2(likedUserTag);

        String jsonPayload = gson.toJson(likeKafkaDto);
        SenderRecord<String, String, String> senderRecord = SenderRecord.create(
                kafkaProps.getTopic(),
                0,
                System.currentTimeMillis(),
                String.valueOf(likeKafkaDto.hashCode()),
                jsonPayload,
                null
        );

        // Асинхронно отправляем сообщение
        return kafkaSender.send(Mono.just(senderRecord))
                .doOnNext(result -> System.out.println("Сообщение успешно отправлено: " + result.correlationMetadata()))
                .doOnError(e -> System.err.println("Ошибка отправки сообщения: " + e.getMessage()))
                .then();

    }

    @Data
    public class LikeKafkaDto{
        private String value1;
        private String value2;
    }

}
