package com.TAstanov.MyChat_Notification;

import com.TAstanov.MyChat_Notification.server.props.KafkaProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({KafkaProperties.class})
public class MyChatNotificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyChatNotificationApplication.class, args);
	}

}
