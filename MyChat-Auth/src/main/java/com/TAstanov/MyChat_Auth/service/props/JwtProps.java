package com.TAstanov.MyChat_Auth.service.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProps {

    private String secretKey;
    private Long accessLiveTime;
    private Long refreshLiveTime;

}
