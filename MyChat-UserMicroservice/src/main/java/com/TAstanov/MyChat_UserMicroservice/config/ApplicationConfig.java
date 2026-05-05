package com.TAstanov.MyChat_UserMicroservice.config;

import com.TAstanov.MyChat_UserMicroservice.service.props.MinioProps;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final MinioProps minioProperties;

    @Bean
    public MinioClient minioClient() {

        System.out.println("MinIO URL: " + minioProperties.getUrl());
        System.out.println("MinIO Access Key: " + minioProperties.getAccessKey());
        System.out.println("MinIO Secret Key: " + minioProperties.getSecretKey());

        return MinioClient.builder()
                .endpoint(minioProperties.getUrl())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
    }

}
