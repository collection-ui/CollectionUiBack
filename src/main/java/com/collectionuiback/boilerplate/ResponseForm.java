package com.collectionuiback.boilerplate;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResponseForm<T> {
    private final int statusCode;
    private final T data;

    @Builder
    public ResponseForm(int statusCode, T data) {
        this.statusCode = statusCode;
        this.data = data;
    }

    public static <T> ResponseForm<T> success(T data) {
        return new ResponseForm<>(HttpStatus.OK.value(), data);
    }
}
