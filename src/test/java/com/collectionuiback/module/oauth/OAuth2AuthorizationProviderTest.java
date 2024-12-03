package com.collectionuiback.module.oauth;

import com.collectionuiback.module.oauth.exception.OAuth2ProviderNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Base64;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OAuth2AuthorizationProviderTest {

    @DisplayName("대상 Provider 가 존재하지 않는 경우 OAuth2ProviderNotFoundException 이 발생한다.")
    @Test
    void raiseOAuth2ProviderNotFoundException() {
        // given
        ClientRegistrationRepository clientRegistrationRepository = new InMemoryClientRegistrationRepository(Collections.emptyMap());
        StringKeyGenerator keyGenerator = new Base64StringKeyGenerator(Base64.getUrlEncoder());

        OAuth2AuthorizationProvider oAuth2AuthorizationProvider = new OAuth2AuthorizationProvider(clientRegistrationRepository, null, null, keyGenerator);

        // expect
        assertThatThrownBy(() -> oAuth2AuthorizationProvider.resolveAuthorizationRequestUri("Nothing"))
                .isInstanceOf(OAuth2ProviderNotFoundException.class);
    }

    @DisplayName("대상 Provider 에 해당하는 RedirectUri 생성")
    @Test
    void resolveRedirectUri() {
        // given
        ClientRegistration clientRegistration = ClientRegistrationFactory.createClientRegistration("registrationId");
        ClientRegistrationRepository clientRegistrationRepository = new InMemoryClientRegistrationRepository(Map.of("registrationId", clientRegistration));

        OAuth2AuthorizationProvider oAuth2AuthorizationProvider = new OAuth2AuthorizationProvider(clientRegistrationRepository, null, null, () -> "randomKey");

        // when
        String redirectUri = oAuth2AuthorizationProvider.resolveAuthorizationRequestUri("registrationId");

        // expect
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(redirectUri).build();

        assertThat(uriComponents.getPath()).isEqualTo("/url");
        assertThat(uriComponents.getHost()).isEqualTo("authorization.com");
        assertThat(uriComponents.getScheme()).isEqualTo("https");

        MultiValueMap<String, String> queryParams = uriComponents.getQueryParams();

        assertThat(queryParams.getFirst("client_id")).isEqualTo("clientId");
        assertThat(queryParams.getFirst("redirect_uri")).isEqualTo("https://redirect.com/url");
        assertThat(queryParams.getFirst("scope")).contains("email", "profile");
        assertThat(queryParams.getFirst("state")).isEqualTo("randomKey");
        assertThat(queryParams.getFirst("response_type")).isEqualTo("code");
    }
}
