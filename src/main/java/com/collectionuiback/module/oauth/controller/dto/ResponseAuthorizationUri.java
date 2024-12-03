package com.collectionuiback.module.oauth.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ResponseAuthorizationUri {

    private final String authorizationUri;

    @Builder
    public ResponseAuthorizationUri(String authorizationUri) {
        this.authorizationUri = authorizationUri;
    }
}
