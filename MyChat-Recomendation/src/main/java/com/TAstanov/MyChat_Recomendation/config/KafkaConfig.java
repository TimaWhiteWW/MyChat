package com.TAstanov.MyChat_Recomendation.config;

import com.TAstanov.MyChat_Recomendation.service.props.KafkaProps;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String servers;

    @Value("${spring.kafka.topics}")
    private List<String> topics;

    private final KafkaProps kafkaProps;


    @Bean
    public Map<String, Object> receiverProperties() {
        Map<String, Object> props = new HashMap<>(5);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProps.getGroupId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProps.getKeyDeserializer());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaProps.getValueDeserializer());
        props.put("spring.json.trusted.packages", kafkaProps.getProperties().get("spring.json.trusted.packages"));
        return props;
    }

    @Bean
    public ReceiverOptions<String, Object> receiverOptions() {
        ReceiverOptions<String, Object> receiverOptions = ReceiverOptions.create(receiverProperties());
        return receiverOptions.subscription(topics).addAssignListener(partition ->
                        System.out.println("assigned: " + partition))
                .addRevokeListener(receiverPartitions ->
                        System.out.println("revoked: " + receiverPartitions));
    }

    @Bean
    public KafkaReceiver<String, Object> receiver(ReceiverOptions<String, Object> options){
        return KafkaReceiver.create(receiverOptions());
    }

}
