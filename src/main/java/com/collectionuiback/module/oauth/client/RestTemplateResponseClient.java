package com.collectionuiback.module.oauth.client;

import com.collectionuiback.module.oauth.exception.OAuth2ClientResponseException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class RestTemplateResponseClient {

    private final RestOperations restOperations;
    private static final ParameterizedTypeReference<Map<String, Object>> PARAMETERIZED_RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    public RestTemplateResponseClient(RestOperations restOperations) {
        this.restOperations = restOperations;
    }

    public <T, U> ResponseEntity<U> getResponse(T request, Converter<T, RequestEntity<?>> converter, Class<U> responseType) {
        return getResponseInternal(converter.convert(request), responseType);
    }

    public <T> ResponseEntity<T> getResponse(Supplier<RequestEntity<?>> requestSupplier, Class<T> responseType) {
        return getResponseInternal(requestSupplier.get(), responseType);
    }

    public <T> T getResponseBody(Supplier<RequestEntity<?>> requestSupplier, Function<Map<String, Object>, T> resultMapper) {
        ResponseEntity<Map<String, Object>> responseEntity = getResponseInternal(requestSupplier.get(), PARAMETERIZED_RESPONSE_TYPE);
        return resultMapper.apply(responseEntity.getBody());
    }

    public Map<String, Object> getResponseBody(Supplier<RequestEntity<?>> requestSupplier) {
        ResponseEntity<Map<String, Object>> responseEntity = getResponseInternal(requestSupplier.get(), PARAMETERIZED_RESPONSE_TYPE);
        return responseEntity.getBody();
    }

    private <T> ResponseEntity<T> getResponseInternal(RequestEntity<?> requestEntity, Class<T> responseType) {
        try {
            return restOperations.exchange(requestEntity, responseType);
        }
        catch (RestClientException e) {
            throw new OAuth2ClientResponseException("Occurred when trying to get response", e);
        }
    }

    private <T> ResponseEntity<T> getResponseInternal(RequestEntity<?> requestEntity, ParameterizedTypeReference<T> responseType) {
        try {
            return restOperations.exchange(requestEntity, responseType);
        }
        catch (RestClientException e) {
            throw new OAuth2ClientResponseException("Occurred when trying to get response", e);
        }
    }
}
