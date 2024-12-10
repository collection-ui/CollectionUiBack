package com.collectionuiback.infra.client;

import com.collectionuiback.module.oauth.exception.OAuth2ClientResponseException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class RestTemplateResponseClient {

    private final RestOperations restOperations;
    private static final ParameterizedTypeReference<Map<String, Object>> PARAMETERIZED_RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    public RestTemplateResponseClient(RestOperations restOperations) {
        this.restOperations = restOperations;
    }

    public RestTemplateResponseClient(ResponseErrorHandler errorHandler, HttpMessageConverter<?> ...messageConverter) {
        RestTemplate restTemplate = new RestTemplate(Arrays.stream(messageConverter).toList());
        restTemplate.setErrorHandler(errorHandler);
        this.restOperations = restTemplate;
    }

    public RestTemplateResponseClient(ResponseErrorHandler errorHandler) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(errorHandler);
        this.restOperations = restTemplate;
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
