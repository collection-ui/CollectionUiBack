package com.collectionuiback.module.oauth.controller;

import com.collectionuiback.module.oauth.ClientRegistrationFactory;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class AuthorizationUriMatcher extends BaseMatcher<String> {
    @Override
    public boolean matches(Object o) {
        if (!(o instanceof String)) {
            return false;
        }
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl((String) o).build();

        String path = uriComponents.getPath();
        String host = uriComponents.getHost();
        String scheme = uriComponents.getScheme();
        MultiValueMap<String, String> queryParams = uriComponents.getQueryParams();
        String clientId = queryParams.getFirst("client_id");
        String redirectUri = queryParams.getFirst("redirect_uri");
        String scope = queryParams.getFirst("scope");
        String state = queryParams.getFirst("state");
        String responseType = queryParams.getFirst("response_type");

        if (!"/url".equals(path)) return false;
        if (!"authorization.com".equals(host)) return false;
        if (!"https".equals(scheme)) return false;
        if (!"clientId".equals(clientId)) return false;
        if (!"https://redirect.com/url".equals(redirectUri)) return false;
        if (scope == null || !scope.contains("email")) return false;
        if (!scope.contains("profile")) return false;
        if (!"stringKey".equals(state)) return false;
        if (!"code".equals(responseType)) return false;

        return true;
    }

    @Override
    public void describeTo(Description description) {
        ClientRegistration clientRegistration = ClientRegistrationFactory.createClientRegistration("registrationId");
        description.appendText("AuthorizationUri is ")
                .appendValue(clientRegistration.getProviderDetails().getAuthorizationUri())
                .appendText(" and query params: ")
                .appendText(" clientId: " + clientRegistration.getClientId())
                .appendText(" redirectUri: " + clientRegistration.getRedirectUri())
                .appendText(" scopes: " + clientRegistration.getScopes())
                .appendText(" responseType: " + clientRegistration.getAuthorizationGrantType().getValue())
                .appendText(" state: stringKey");
    }
}
