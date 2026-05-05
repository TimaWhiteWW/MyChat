package com.TAstanov.MyChat_Recomendation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableKafka
@EnableScheduling
public class MyChatRecomendationApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyChatRecomendationApplication.class, args);
	}

}
