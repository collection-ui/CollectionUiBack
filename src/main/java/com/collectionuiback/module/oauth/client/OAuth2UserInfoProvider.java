package com.collectionuiback.module.oauth.client;

import com.collectionuiback.infra.client.RestTemplateResponseClient;
import com.collectionuiback.module.oauth.OAuth2Attributes;
import com.collectionuiback.module.oauth.exception.OAuth2ClientResponseException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Component
public class OAuth2UserInfoProvider {

    private final RestTemplateResponseClient client;

    public OAuth2UserInfoProvider(@Qualifier("userInfo") RestTemplateResponseClient client) {
        this.client = client;
    }

    public OAuth2Attributes getUserInfo(String accessToken, ClientRegistration clientRegistration) {
        Map<String, Object> responseBody = getResponseBody(accessToken, clientRegistration);

        String registrationId = clientRegistration.getRegistrationId();
        String userNameAttributeName = clientRegistration.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        return OAuth2Attributes.of(registrationId, userNameAttributeName, responseBody);
    }

    private Map<String, Object> getResponseBody(String accessToken, ClientRegistration clientRegistration) {
        try {
            return client.getResponseBody(() -> {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));

                URI uri = UriComponentsBuilder
                        .fromUriString(clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri())
                        .build()
                        .toUri();

                if (isPostMethod(clientRegistration)) {
                    httpHeaders.setContentType(MediaType
                            .valueOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8"));

                    LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
                    params.add(OAuth2ParameterNames.ACCESS_TOKEN, accessToken);

                    return new RequestEntity<>(params, httpHeaders, HttpMethod.POST, uri);
                }

                httpHeaders.setBearerAuth(accessToken);

                return new RequestEntity<>(httpHeaders, HttpMethod.GET, uri);
            }, result -> result);
        }
        catch (RestClientException e) {
            throw new OAuth2ClientResponseException("Occurred RestClient Exception when getUserInfo", e);
        }
    }

    private boolean isPostMethod(ClientRegistration clientRegistration) {
        return AuthenticationMethod.FORM.equals(clientRegistration.getProviderDetails().getUserInfoEndpoint().getAuthenticationMethod());
    }
}
