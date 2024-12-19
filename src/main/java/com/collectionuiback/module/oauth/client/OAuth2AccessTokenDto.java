package com.collectionuiback.module.oauth.client;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuth2AccessTokenDto {
    private final String accessToken;
    private final String expiresIn;
    private final String scope;
    private final String tokenType;
    private final String idToken;

    @Builder
    public OAuth2AccessTokenDto(String accessToken, String expiresIn, String scope, String tokenType, String idToken) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.scope = scope;
        this.tokenType = tokenType;
        this.idToken = idToken;
    }
}
