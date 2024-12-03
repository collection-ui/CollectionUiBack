package com.collectionuiback.module.oauth.client;

import com.collectionuiback.infra.client.RestTemplateResponseClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class OAuth2AccessTokenProvider {

    private final RestTemplateResponseClient client;

    public OAuth2AccessTokenProvider(@Qualifier("accessToken") RestTemplateResponseClient client) {
        this.client = client;
    }

    public OAuth2AccessTokenResponse getAccessToken(String code, ClientRegistration clientRegistration) {
        return getAccessToken(code, clientRegistration, clientRegistration.getRedirectUri());
    }

    public OAuth2AccessTokenResponse getAccessToken(String code, ClientRegistration clientRegistration, String redirectUri) {
        ResponseEntity<OAuth2AccessTokenResponse> response = client.getResponse(() -> {
            HttpHeaders httpHeaders = createHeaders(clientRegistration);

            LinkedMultiValueMap<String, String> params = createParameters(code, clientRegistration, redirectUri);

            URI uri = UriComponentsBuilder
                    .fromUriString(clientRegistration.getProviderDetails().getTokenUri())
                    .build()
                    .toUri();

            return new RequestEntity<>(params, httpHeaders, HttpMethod.POST, uri);
        }, OAuth2AccessTokenResponse.class);

        OAuth2AccessTokenResponse body = response.getBody();
        return body;
    }

    private HttpHeaders createHeaders(ClientRegistration clientRegistration) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        if (ClientAuthenticationMethod.CLIENT_SECRET_BASIC.equals(clientRegistration.getClientAuthenticationMethod())) {
            String clientId = URLEncoder.encode(clientRegistration.getClientId(), StandardCharsets.UTF_8);
            String clientSecret = URLEncoder.encode(clientRegistration.getClientSecret(), StandardCharsets.UTF_8);
            httpHeaders.setBasicAuth(clientId, clientSecret);
        }
        return httpHeaders;
    }

    private LinkedMultiValueMap<String, String> createParameters(String code, ClientRegistration clientRegistration, String redirectUri) {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(OAuth2ParameterNames.GRANT_TYPE, clientRegistration.getAuthorizationGrantType().getValue());
        params.add(OAuth2ParameterNames.CODE, code);
        params.add(OAuth2ParameterNames.REDIRECT_URI, redirectUri);
        if (!ClientAuthenticationMethod.CLIENT_SECRET_BASIC
                .equals(clientRegistration.getClientAuthenticationMethod())) {
            params.add(OAuth2ParameterNames.CLIENT_ID, clientRegistration.getClientId());
        }
        if (ClientAuthenticationMethod.CLIENT_SECRET_POST.equals(clientRegistration.getClientAuthenticationMethod())) {
            params.add(OAuth2ParameterNames.CLIENT_SECRET, clientRegistration.getClientSecret());
        }
        return params;
    }
}
