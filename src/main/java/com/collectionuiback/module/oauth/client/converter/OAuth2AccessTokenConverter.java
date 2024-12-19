package com.collectionuiback.module.oauth.client.converter;

import com.collectionuiback.module.oauth.client.OAuth2AccessTokenDto;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component
public class OAuth2AccessTokenConverter implements Function<Map<String, Object>, OAuth2AccessTokenDto> {
    @Override
    public OAuth2AccessTokenDto apply(Map<String, Object> stringObjectMap) {
        return OAuth2AccessTokenDto.builder()
                .accessToken((String) stringObjectMap.get("access_token"))
                .expiresIn((String) stringObjectMap.get("expires_in"))
                .scope((String) stringObjectMap.get("scope"))
                .tokenType((String) stringObjectMap.get("token_type"))
                .idToken((String) stringObjectMap.get("id_token"))
                .build();
    }
}
