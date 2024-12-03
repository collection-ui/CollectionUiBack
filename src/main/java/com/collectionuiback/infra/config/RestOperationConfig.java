package com.collectionuiback.infra.config;

import com.collectionuiback.infra.client.RestTemplateResponseClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;

@Configuration
public class RestOperationConfig {

    @Qualifier("accessToken")
    @Bean
    public RestTemplateResponseClient oAuth2AccessTokenResponseClient() {
        return new RestTemplateResponseClient(
                new OAuth2ErrorResponseErrorHandler(),
                new FormHttpMessageConverter(), new OAuth2AccessTokenResponseHttpMessageConverter()
        );
    }

    @Qualifier("userInfo")
    @Bean
    public RestTemplateResponseClient oAuth2UserInfoResponseClient() {
        return new RestTemplateResponseClient(
                new OAuth2ErrorResponseErrorHandler()
        );
    }
}
