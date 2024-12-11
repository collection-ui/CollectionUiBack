package com.collectionuiback.module.exception;

import com.collectionuiback.boilerplate.ErrorResponseForm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

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

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponseForm> notFoundException(HttpServletRequest request, NoHandlerFoundException e) {
        log.error("Occurred NotFound Exception: [{}:{}]", request.getMethod(), request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponseForm.notFound());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseForm> methodNotAllowedException(HttpServletRequest request, HttpRequestMethodNotSupportedException e) {
        log.error("Occurred MethodNotAllowed Exception: [{}:{}]", request.getMethod(), request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ErrorResponseForm.methodNotAllowed());
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ErrorResponseForm> notAcceptableException(HttpServletRequest request, HttpMediaTypeNotAcceptableException e) {
        log.error("Occurred NotAcceptable Exception: [{}:{}][Accept:{}]", request.getMethod(), request.getRequestURI(), request.getHeader("Accept"));

        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorResponseForm.notAcceptable());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponseForm> mediaTypeNotSupportedException(HttpServletRequest request, HttpMediaTypeNotSupportedException e) {
        log.error("Occurred MediaTypeNotSupportedException: [{}:{}:{}]", request.getMethod(), request.getRequestURI(), request.getContentType());

        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(ErrorResponseForm.unsupportedMediaType());
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponseForm> handleServiceException(ServiceException e) {
        ErrorResponseForm errorResponseForm = e.toErrorResponseForm();

        log.error("Occurred ServiceException: {}", e.getMessage(), e);

        return ResponseEntity
                .status(errorResponseForm.getStatusCode())
                .body(errorResponseForm);
    }
}
