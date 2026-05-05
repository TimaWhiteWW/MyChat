package com.TAstanov.MyChat_Swipe.service.props;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "redis-props")
public class RedisProps {

    private Integer expirationHours;

}
