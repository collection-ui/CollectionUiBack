package com.collectionuiback.module.exception;

import com.collectionuiback.boilerplate.ErrorResponseForm;

public abstract class ServiceException extends RuntimeException {
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(String message) {
        super(message);
    }

    abstract public ErrorResponseForm toErrorResponseForm();
}
