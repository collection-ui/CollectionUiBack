package com.collectionuiback.boilerplate;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponseForm {
    private final int code;
    private final Object data;
    private final String message;

    @Builder
    public ErrorResponseForm(int code, Object data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public static ErrorResponseForm clientError(Object data, String message) {
        return ErrorResponseForm.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .data(data)
                .message(message)
                .build();
    }

    public static ErrorResponseForm serverError(Object data, String message) {
        return ErrorResponseForm.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .data(data)
                .message(message)
                .build();
    }
}
