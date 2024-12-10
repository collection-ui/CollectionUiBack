package com.collectionuiback.boilerplate;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponseForm {
    private final int statusCode;
    private final Object data;
    private final String message;

    @Builder
    public ErrorResponseForm(int statusCode, Object data, String message) {
        this.statusCode = statusCode;
        this.data = data;
        this.message = message;
    }

    public static ErrorResponseForm clientError(Object data, String message) {
        return ErrorResponseForm.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .data(data)
                .message(message)
                .build();
    }

    public static ErrorResponseForm serverError(Object data, String message) {
        return ErrorResponseForm.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .data(data)
                .message(message)
                .build();
    }
}
