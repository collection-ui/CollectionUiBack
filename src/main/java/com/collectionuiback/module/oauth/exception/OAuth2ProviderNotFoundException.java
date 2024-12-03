package com.collectionuiback.module.oauth.exception;

import com.collectionuiback.boilerplate.ErrorResponseForm;
import com.collectionuiback.module.exception.ServiceException;

public class OAuth2ProviderNotFoundException extends ServiceException {
    public OAuth2ProviderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public OAuth2ProviderNotFoundException(String message) {
        super(message);
    }

    @Override
    public ErrorResponseForm toErrorResponseForm() {
        return ErrorResponseForm.clientError(null, getMessage());
    }
}
