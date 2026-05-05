package com.TAstanov.MyChat_Auth;

import com.TAstanov.MyChat_Auth.service.props.JwtProps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties(JwtProps.class)
@EnableScheduling
public class MyChatAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyChatAuthApplication.class, args);
	}

}
