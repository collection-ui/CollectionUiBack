package com.collectionuiback.module.oauth.controller.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestLoginByCode {
    private String code;

    public RequestLoginByCode(String code) {
        this.code = code;
    }
}
