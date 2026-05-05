package com.TAstanov.MyChat_Swipe.service.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "beauty-adjust")
public class AdjustProps {

    private Double likeUniversalAdjust;
    private Double skipUniversalAdjust;
    private Double saveBatchSize;

}
