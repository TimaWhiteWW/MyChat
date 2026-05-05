package com.TAstanov.MyChat_Recomendation.service.props;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "spring.kafka.consumer")
public class KafkaProps {

    private String groupId;
    private String keyDeserializer;
    private String valueDeserializer;
    private Map<String, String> properties;

}
