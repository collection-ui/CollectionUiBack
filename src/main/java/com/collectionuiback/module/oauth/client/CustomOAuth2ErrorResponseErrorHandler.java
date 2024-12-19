package com.collectionuiback.module.oauth.client;

import com.collectionuiback.module.oauth.exception.OAuth2ClientResponseException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@Component
public class CustomOAuth2ErrorResponseErrorHandler implements ResponseErrorHandler {

    private final ResponseErrorHandler delegate = new OAuth2ErrorResponseErrorHandler();

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return delegate.hasError(response);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        try {
            delegate.handleError(response);
        } catch (OAuth2AuthorizationException e) {
            throw new OAuth2ClientResponseException("Occurred OAuth2 Client Exception", e);
        }
    }
}
