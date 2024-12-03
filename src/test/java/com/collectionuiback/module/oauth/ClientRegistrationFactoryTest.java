package com.collectionuiback.module.oauth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ClientRegistrationFactoryTest {

    @DisplayName("ClientRegistration 생성 테스트")
    @Test
    void createClientRegistration() {
        ClientRegistration clientRegistration = ClientRegistrationFactory.createClientRegistration("registrationId");

        assertThat(clientRegistration.getRegistrationId()).isEqualTo("registrationId");
        assertThat(clientRegistration.getClientAuthenticationMethod()).isEqualTo(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
        assertThat(clientRegistration.getAuthorizationGrantType()).isEqualTo(AuthorizationGrantType.AUTHORIZATION_CODE);
        assertThat(clientRegistration.getRedirectUri()).isEqualTo("https://redirect.com/url");
        assertThat(clientRegistration.getScopes()).isNotEmpty();
        assertThat(clientRegistration.getScopes()).contains("profile", "email");
        assertThat(clientRegistration.getProviderDetails().getAuthorizationUri()).isEqualTo("https://authorization.com/url");
        assertThat(clientRegistration.getProviderDetails().getTokenUri()).isEqualTo("https://token.com/url");
        assertThat(clientRegistration.getProviderDetails().getJwkSetUri()).isEqualTo("https://jwkset.com/url");
        assertThat(clientRegistration.getProviderDetails().getIssuerUri()).isEqualTo("https://issuer.com/url");
        assertThat(clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri()).isEqualTo("https://userinfo.com/url");
        assertThat(clientRegistration.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName()).isEqualTo(IdTokenClaimNames.SUB);
        assertThat(clientRegistration.getClientName()).isEqualTo("registrationId");
        assertThat(clientRegistration.getClientId()).isEqualTo("clientId");
        assertThat(clientRegistration.getClientSecret()).isEqualTo("clientSecret");
        assertThat(clientRegistration.getProviderDetails().getUserInfoEndpoint().getAuthenticationMethod()).isEqualTo(AuthenticationMethod.HEADER);
    }

    @DisplayName("UserInfo Method Form 을 가진 ClientRegistration 생성 테스트")
    @Test
    void createClientRegistrationWithFormUserInfoAuthenticationMethod() {
        ClientRegistration clientRegistration = ClientRegistrationFactory.createClientRegistrationWithFormUserInfoAuthenticationMethod("registrationId");

        assertThat(clientRegistration.getRegistrationId()).isEqualTo("registrationId");
        assertThat(clientRegistration.getClientAuthenticationMethod()).isEqualTo(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
        assertThat(clientRegistration.getAuthorizationGrantType()).isEqualTo(AuthorizationGrantType.AUTHORIZATION_CODE);
        assertThat(clientRegistration.getRedirectUri()).isEqualTo("https://redirect.com/url");
        assertThat(clientRegistration.getScopes()).isNotEmpty();
        assertThat(clientRegistration.getScopes()).contains("profile", "email");
        assertThat(clientRegistration.getProviderDetails().getAuthorizationUri()).isEqualTo("https://authorization.com/url");
        assertThat(clientRegistration.getProviderDetails().getTokenUri()).isEqualTo("https://token.com/url");
        assertThat(clientRegistration.getProviderDetails().getJwkSetUri()).isEqualTo("https://jwkset.com/url");
        assertThat(clientRegistration.getProviderDetails().getIssuerUri()).isEqualTo("https://issuer.com/url");
        assertThat(clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri()).isEqualTo("https://userinfo.com/url");
        assertThat(clientRegistration.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName()).isEqualTo(IdTokenClaimNames.SUB);
        assertThat(clientRegistration.getClientName()).isEqualTo("registrationId");
        assertThat(clientRegistration.getClientId()).isEqualTo("clientId");
        assertThat(clientRegistration.getClientSecret()).isEqualTo("clientSecret");
        assertThat(clientRegistration.getProviderDetails().getUserInfoEndpoint().getAuthenticationMethod()).isEqualTo(AuthenticationMethod.FORM);
    }

    @DisplayName("ClientAuthenticationMethod 가 ClientSecretPost 인 ClientRegistration 생성 테스트")
    @Test
    void createClientRegistrationWithClientSecretPostAuthenticationMethod() {
        ClientRegistration clientRegistration = ClientRegistrationFactory.createClientRegistrationWithClientSecretPost("registrationId");

        assertThat(clientRegistration.getRegistrationId()).isEqualTo("registrationId");
        assertThat(clientRegistration.getClientAuthenticationMethod()).isEqualTo(ClientAuthenticationMethod.CLIENT_SECRET_POST);
        assertThat(clientRegistration.getAuthorizationGrantType()).isEqualTo(AuthorizationGrantType.AUTHORIZATION_CODE);
        assertThat(clientRegistration.getRedirectUri()).isEqualTo("https://redirect.com/url");
        assertThat(clientRegistration.getScopes()).isNotEmpty();
        assertThat(clientRegistration.getScopes()).contains("profile", "email");
        assertThat(clientRegistration.getProviderDetails().getAuthorizationUri()).isEqualTo("https://authorization.com/url");
        assertThat(clientRegistration.getProviderDetails().getTokenUri()).isEqualTo("https://token.com/url");
        assertThat(clientRegistration.getProviderDetails().getJwkSetUri()).isEqualTo("https://jwkset.com/url");
        assertThat(clientRegistration.getProviderDetails().getIssuerUri()).isEqualTo("https://issuer.com/url");
        assertThat(clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri()).isEqualTo("https://userinfo.com/url");
        assertThat(clientRegistration.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName()).isEqualTo(IdTokenClaimNames.SUB);
        assertThat(clientRegistration.getClientName()).isEqualTo("registrationId");
        assertThat(clientRegistration.getClientId()).isEqualTo("clientId");
        assertThat(clientRegistration.getClientSecret()).isEqualTo("clientSecret");
        assertThat(clientRegistration.getProviderDetails().getUserInfoEndpoint().getAuthenticationMethod()).isEqualTo(AuthenticationMethod.HEADER);
    }
}
