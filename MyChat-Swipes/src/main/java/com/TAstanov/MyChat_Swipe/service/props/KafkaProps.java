package com.TAstanov.MyChat_Swipe.service.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "kafka")
public class KafkaProps {
    private String bootstrapServers;
    private String topic;
}
