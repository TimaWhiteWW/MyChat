package com.TAstanov.MyChat_Notification.server.Impl;

import com.TAstanov.MyChat_Notification.server.KafkaListener;
import com.TAstanov.MyChat_Notification.server.MessageProcessingService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaListenerImpl implements KafkaListener {

    private final KafkaReceiver<String, Object> kafkaReceiver;
    private final Gson gson = new GsonBuilder().create();
    private final MessageProcessingService messageProcessingService;

    @PostConstruct
    public void init() {
        fetch();
    }

    @Override
    public void fetch() {
        kafkaReceiver.receive().subscribe(this::processMessage);
    }

    private void processMessage(ReceiverRecord<String, Object> stringObjectReceiverRecord) {

        LikeKafkaDto likeKafkaDto = gson.fromJson(stringObjectReceiverRecord.value().toString(), LikeKafkaDto.class);

        messageProcessingService.process(likeKafkaDto);

        acknowledgeOffsetAsync(stringObjectReceiverRecord);

    }

    private void acknowledgeOffsetAsync(ReceiverRecord<String, Object> receiverRecord) {
        CompletableFuture.runAsync(() -> {
            try {
                receiverRecord.receiverOffset().acknowledge();
            } catch (Exception e) {
                log.error("Error while processing kafka message");
            }
        });
    }

    @Data
    public class LikeKafkaDto {
        private String value1;
        private String value2;
    }

}
