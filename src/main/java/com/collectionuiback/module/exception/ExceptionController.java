package com.collectionuiback.module.exception;

import com.collectionuiback.boilerplate.ErrorResponseForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseForm> handleException(Exception e) {
        log.error("Occurred Uncaught Exception: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponseForm.serverError(null, "Occurred Uncaught Exception"));
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponseForm> handleServiceException(ServiceException e) {
        ErrorResponseForm errorResponseForm = e.toErrorResponseForm();

        log.error("Occurred ServiceException: {}", e.getMessage(), e);
        return ResponseEntity
                .status(errorResponseForm.getCode())
                .body(errorResponseForm);
    }
}
