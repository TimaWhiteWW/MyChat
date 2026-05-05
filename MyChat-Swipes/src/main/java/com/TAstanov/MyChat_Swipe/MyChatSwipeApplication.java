package com.TAstanov.MyChat_Swipe;

import com.TAstanov.MyChat_Swipe.service.props.AdjustProps;
import com.TAstanov.MyChat_Swipe.service.props.KafkaProps;
import com.TAstanov.MyChat_Swipe.service.props.PostgresProps;
import com.TAstanov.MyChat_Swipe.service.props.RedisProps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties({KafkaProps.class, AdjustProps.class, RedisProps.class, PostgresProps.class})
@EnableScheduling
public class MyChatSwipeApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyChatSwipeApplication.class, args);
	}

}
