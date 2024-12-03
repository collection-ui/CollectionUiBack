package com.collectionuiback.module.oauth;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;

public class ClientRegistrationFactory {

    public static ClientRegistration createClientRegistration(String registrationId) {
        ClientRegistration.Builder builder = getDefaultBuilder(registrationId);

        return builder.build();
    }

    public static ClientRegistration createClientRegistrationWithFormUserInfoAuthenticationMethod(String registrationId) {
        ClientRegistration.Builder builder = getDefaultBuilder(registrationId);
        builder.userInfoAuthenticationMethod(AuthenticationMethod.FORM);

        return builder.build();
    }

    public static ClientRegistration createClientRegistrationWithClientSecretPost(String registrationId) {
        ClientRegistration.Builder builder = getDefaultBuilder(registrationId);
        builder.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST);

        return builder.build();
    }

    public static ClientRegistration createClientRegistrationWithAuthorizationGrantType(String registrationId, AuthorizationGrantType grantType) {
        ClientRegistration.Builder builder = getDefaultBuilder(registrationId);
        builder.authorizationGrantType(grantType);

        return builder.build();
    }

    private static ClientRegistration.Builder getDefaultBuilder(String registrationId) {
        ClientRegistration.Builder builder = ClientRegistration.withRegistrationId(registrationId);
        builder.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
        builder.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE);
        builder.redirectUri("https://redirect.com/url");
        builder.scope("profile", "email");
        builder.authorizationUri("https://authorization.com/url");
        builder.tokenUri("https://token.com/url");
        builder.jwkSetUri("https://jwkset.com/url");
        builder.issuerUri("https://issuer.com/url");
        builder.userInfoUri("https://userinfo.com/url");
        builder.userNameAttributeName(IdTokenClaimNames.SUB);
        builder.clientName(registrationId);
        builder.clientId("clientId");
        builder.clientSecret("clientSecret");
        return builder;
    }
}
