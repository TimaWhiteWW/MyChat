package com.TAstanov.MyChat_Recomendation.service.props;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "elasticsearch-props")
@Data
public class ElasticsearchProps {

    private Integer minShardNumber;
    private Integer maxShardNumber;

}
