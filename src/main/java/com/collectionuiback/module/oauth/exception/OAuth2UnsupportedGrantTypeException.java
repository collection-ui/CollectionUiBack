package com.collectionuiback.module.oauth.exception;

import com.collectionuiback.boilerplate.ErrorResponseForm;
import com.collectionuiback.module.exception.ServiceException;

public class OAuth2UnsupportedGrantTypeException extends ServiceException {

    public OAuth2UnsupportedGrantTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public OAuth2UnsupportedGrantTypeException(String message) {
        super(message);
    }

    @Override
    public ErrorResponseForm toErrorResponseForm() {
        return ErrorResponseForm.serverError(null, getMessage());
    }
}
