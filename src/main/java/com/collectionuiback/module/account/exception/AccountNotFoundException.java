package com.collectionuiback.module.account.exception;

import com.collectionuiback.boilerplate.ErrorResponseForm;
import com.collectionuiback.module.exception.ServiceException;

public class AccountNotFoundException extends ServiceException {
    public AccountNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountNotFoundException(String message) {
        super(message);
    }

    @Override
    public ErrorResponseForm toErrorResponseForm() {
        return ErrorResponseForm.clientError(null, getMessage());
    }
}
