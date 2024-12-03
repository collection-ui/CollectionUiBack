package com.collectionuiback.module.oauth;

import com.collectionuiback.module.oauth.client.OAuth2AccessTokenProvider;
import com.collectionuiback.module.oauth.client.OAuth2UserInfoProvider;
import com.collectionuiback.module.oauth.exception.OAuth2ProviderNotFoundException;
import com.collectionuiback.module.oauth.exception.OAuth2UnsupportedGrantTypeException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OAuth2AuthorizationProvider {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2AccessTokenProvider oAuth2AccessTokenProvider;
    private final OAuth2UserInfoProvider oAuth2UserInfoProvider;
    private final StringKeyGenerator stringKeyGenerator;

    public String resolveAuthorizationRequestUri(String registrationId) {
        ClientRegistration clientRegistration = findByRegistrationId(registrationId);

        OAuth2AuthorizationRequest oAuth2AuthorizationRequest = getBuilder(clientRegistration)
                .clientId(clientRegistration.getClientId())
                .authorizationUri(clientRegistration.getProviderDetails().getAuthorizationUri())
                .redirectUri(clientRegistration.getRedirectUri())
                .scopes(clientRegistration.getScopes())
                .state(stringKeyGenerator.generateKey())
                .build();

        return oAuth2AuthorizationRequest.getAuthorizationRequestUri();
    }

    private OAuth2AuthorizationRequest.Builder getBuilder(ClientRegistration clientRegistration) {
        if (AuthorizationGrantType.AUTHORIZATION_CODE.equals(clientRegistration.getAuthorizationGrantType())) {
            return OAuth2AuthorizationRequest.authorizationCode()
                    .attributes(attrs -> attrs.put(OAuth2ParameterNames.REGISTRATION_ID, clientRegistration.getRegistrationId()));
        }

        throw new OAuth2UnsupportedGrantTypeException("Unsupported grant type: " + clientRegistration.getAuthorizationGrantType().getValue());
    }

    public OAuth2Attributes loadAuthorizationByCode(String registrationId, String code) {
        ClientRegistration clientRegistration = findByRegistrationId(registrationId);

        OAuth2AccessTokenResponse accessToken = oAuth2AccessTokenProvider.getAccessToken(code, clientRegistration);

        return oAuth2UserInfoProvider.getUserInfo(accessToken.getAccessToken().getTokenValue(), clientRegistration);
    }

    private ClientRegistration findByRegistrationId(String registrationId) {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);
        if (clientRegistration == null) {
            throw new OAuth2ProviderNotFoundException("Client registration not found: " + registrationId);
        }

        return clientRegistration;
    }
}
