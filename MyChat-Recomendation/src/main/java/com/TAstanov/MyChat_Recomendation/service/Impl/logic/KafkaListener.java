package com.TAstanov.MyChat_Recomendation.service.Impl.logic;

import com.TAstanov.MyChat_Recomendation.domain.user.UserPropertiesToVectorize;
import com.TAstanov.MyChat_Recomendation.service.KafkaDataReceiver;
import com.TAstanov.MyChat_Recomendation.service.UserVectorizeService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaListener implements KafkaDataReceiver {

    private final KafkaReceiver<String, Object> receiver;
    private final UserVectorizeService userVectorize;
    private final Gson gson = new GsonBuilder().create();

    @PostConstruct
    private void init() {
        fetch();
    }

    @Override
    public void fetch() {
        receiver.receive().subscribe(this::processMessage);
    }

    private void processMessage(ReceiverRecord<String, Object> record){
        try {
            // Парсим данные
            UserPropertiesToVectorize properties = gson.fromJson(record.value().toString(), UserPropertiesToVectorize.class);

            // Векторизация данных
            userVectorize.vectorize(properties);


            acknowledgeOffsetAsync(record);

            log.info("Обработано Kafka-сообщение для пользователя {}", properties.getUserTag());
        } catch (JsonSyntaxException e) {
            log.error("Ошибка при парсинге JSON для сообщения: {}", record.value(), e);
        } catch (Exception e) {
            log.error("Ошибка при обработке Kafka-сообщения для пользователя: {}", e.getMessage());
        }
    }

        private void acknowledgeOffsetAsync (ReceiverRecord<String, Object> record){
        CompletableFuture.runAsync(() -> {
            try {
                record.receiverOffset().acknowledge();
            } catch (Exception e) {
                log.error("Ошибка при подтверждении смещения Kafka-сообщения", e);
            }
        });
    }
}
