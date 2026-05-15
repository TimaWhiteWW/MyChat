package com.TAstanov.MyChat_Notification.config;

import com.TAstanov.MyChat_Notification.server.props.KafkaProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;


import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "mychat.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
public class KafkaConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public Map<String, Object> receiverProperties(){
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getServer());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getGroupId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getKeyDeserializer());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getValueDeserializer());
        props.put("spring.json.trusted.packages", kafkaProperties.getTrustedPackages());
        return props;
    }

    @Bean
    public ReceiverOptions<String, Object> receiverOptions(){
        ReceiverOptions<String, Object> receiverOptions = ReceiverOptions.create(receiverProperties());
        return receiverOptions.subscription(kafkaProperties.getTopics());
    }

    @Bean
    public KafkaReceiver<String, Object> kafkaReceiver(){
        return KafkaReceiver.create(receiverOptions());
    }

}
