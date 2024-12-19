package com.collectionuiback.infra.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Configuration
public class RestOperationConfig {

    @Bean
    public RestOperations restOperations(ResponseErrorHandler errorHandler) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(errorHandler);
        return restTemplate;
    }
}
