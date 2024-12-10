package com.collectionuiback.infra.config;

import com.collectionuiback.infra.client.RestTemplateResponseClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;

@RequiredArgsConstructor
@Configuration
public class RestOperationConfig {

    private final ResponseErrorHandler responseErrorHandler;

    @Qualifier("accessToken")
    @Bean
    public RestTemplateResponseClient oAuth2AccessTokenResponseClient() {
        return new RestTemplateResponseClient(
                responseErrorHandler,
                new FormHttpMessageConverter(), new OAuth2AccessTokenResponseHttpMessageConverter()
        );
    }

    @Qualifier("userInfo")
    @Bean
    public RestTemplateResponseClient oAuth2UserInfoResponseClient() {
        return new RestTemplateResponseClient(
                responseErrorHandler
        );
    }
}
