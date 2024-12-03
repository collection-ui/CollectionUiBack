package com.collectionuiback.boilerplate;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResponseForm {
    private final int code;
    private final Object data;

    @Builder
    public ResponseForm(int code, Object data) {
        this.code = code;
        this.data = data;
    }

    public static ResponseForm success(Object data) {
        return new ResponseForm(HttpStatus.OK.value(), data);
    }
}
