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

    public static ErrorResponseForm unauthorized() {
        return ErrorResponseForm.builder()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .message("Unauthorized")
                .data(null)
                .build();
    }

    public static ErrorResponseForm accessDenied() {
        return ErrorResponseForm.builder()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .message("Access denied")
                .data(null)
                .build();
    }

    public static ErrorResponseForm notFound() {
        return ErrorResponseForm.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message("Not Found")
                .data(null)
                .build();
    }

    public static ErrorResponseForm methodNotAllowed() {
        return ErrorResponseForm.builder()
                .statusCode(HttpStatus.METHOD_NOT_ALLOWED.value())
                .message("Method Not Allowed")
                .data(null)
                .build();
    }

    public static ErrorResponseForm notAcceptable() {
        return ErrorResponseForm.builder()
                .statusCode(HttpStatus.NOT_ACCEPTABLE.value())
                .message("Not Acceptable")
                .data(null)
                .build();
    }

    public static ErrorResponseForm unsupportedMediaType() {
        return ErrorResponseForm.builder()
                .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .message("Unsupported MediaType")
                .data(null)
                .build();
    }
}
