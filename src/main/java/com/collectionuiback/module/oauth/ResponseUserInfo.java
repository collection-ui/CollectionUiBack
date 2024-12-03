package com.collectionuiback.module.oauth;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ResponseUserInfo {

    private final String email;
    private final String name;
    private final String picture;
    private final String role;

    @Builder
    public ResponseUserInfo(String email, String name, String picture, String role) {
        this.email = email;
        this.name = name;
        this.picture = picture;
        this.role = role;
    }
}
