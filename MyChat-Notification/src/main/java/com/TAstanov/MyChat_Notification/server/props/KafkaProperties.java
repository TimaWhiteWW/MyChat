package com.TAstanov.MyChat_Notification.server.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProperties {

    private String server;
    private String groupId;
    private String trustedPackages;
    private String keyDeserializer;
    private String valueDeserializer;
    private List<String> topics;

}
