package com.TAstanov.MyChat_LoadedTest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @Scope("prototype")
    public WebClient webClient(){
        return WebClient.builder().build();
    }

}
