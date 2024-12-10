package com.collectionuiback.module.oauth.client;

import com.collectionuiback.infra.client.RestTemplateResponseClient;
import com.collectionuiback.module.oauth.ClientRegistrationFactory;
import com.collectionuiback.module.oauth.exception.OAuth2ClientResponseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest
class OAuth2AccessTokenProviderTest {

    @Autowired
    ObjectMapper objectMapper;

    @DisplayName("TokenUri 로 Code 를 담아 요청하면 AccessToken 이 반환된다. CLIENT_SECRET_BASIC 으로 요청시 헤더에 Authorization 이 포함된다.")
    @Test
    void requestTokenUriWithCodeTest() throws JsonProcessingException {
        // given
        RestTemplate restTemplate = new RestTemplate(List.of(new FormHttpMessageConverter(), new OAuth2AccessTokenResponseHttpMessageConverter()));
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();

        RestTemplateResponseClient restTemplateResponseClient = new RestTemplateResponseClient(restTemplate);
        OAuth2AccessTokenProvider oAuth2AccessTokenProvider = new OAuth2AccessTokenProvider(restTemplateResponseClient);
        ClientRegistration clientRegistration = ClientRegistrationFactory.createClientRegistration("clientRegistration");

        // when
        Map<String, String> responseMap = Map.of(
                "access_token", "accessTokenValue",
                "expires_in", "3600",
                "scope", "email, profile",
                "token_type", "Bearer",
                "id_token", "idTokenValue"
        );
        String responseJson = objectMapper.writeValueAsString(responseMap);

        server
                .expect(requestTo(clientRegistration.getProviderDetails().getTokenUri()))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", notNullValue()))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        OAuth2AccessTokenResponse accessToken = oAuth2AccessTokenProvider.getAccessToken("code", clientRegistration);

        // then
        assertThat(accessToken).isNotNull();
        assertThat(accessToken.getAccessToken()).isNotNull();
        assertThat(accessToken.getAccessToken().getTokenValue()).isEqualTo("accessTokenValue");
        assertThat(accessToken.getAccessToken().getTokenType()).isEqualTo(OAuth2AccessToken.TokenType.BEARER);
    }

    @DisplayName("TokenUri 로 Code 를 담아 요청하면 AccessToken 이 반환된다. CLIENT_SECRET_POST 의 경우 헤더에 Authorization 이 포함되지 않는다.")
    @Test
    void requestTokenUriWithCodeTestOnClientSecretPost() throws JsonProcessingException {
        // given
        RestTemplate restTemplate = new RestTemplate(List.of(new FormHttpMessageConverter(), new OAuth2AccessTokenResponseHttpMessageConverter()));
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();

        RestTemplateResponseClient restTemplateResponseClient = new RestTemplateResponseClient(restTemplate);
        OAuth2AccessTokenProvider oAuth2AccessTokenProvider = new OAuth2AccessTokenProvider(restTemplateResponseClient);
        ClientRegistration clientRegistration = ClientRegistrationFactory.createClientRegistrationWithClientSecretPost("clientRegistration");

        // when
        Map<String, String> responseMap = Map.of(
                "access_token", "accessTokenValue",
                "expires_in", "3600",
                "scope", "email, profile",
                "token_type", "Bearer",
                "id_token", "idTokenValue"
        );
        String responseJson = objectMapper.writeValueAsString(responseMap);

        server
                .expect(requestTo(clientRegistration.getProviderDetails().getTokenUri()))
                .andExpect(method(HttpMethod.POST))
                .andExpect(headerDoesNotExist("Authorization"))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        OAuth2AccessTokenResponse accessToken = oAuth2AccessTokenProvider.getAccessToken("code", clientRegistration);

        // then
        assertThat(accessToken).isNotNull();
        assertThat(accessToken.getAccessToken()).isNotNull();
        assertThat(accessToken.getAccessToken().getTokenValue()).isEqualTo("accessTokenValue");
        assertThat(accessToken.getAccessToken().getTokenType()).isEqualTo(OAuth2AccessToken.TokenType.BEARER);
    }

    @DisplayName("AccessTokenProvider 에서 404 에러가 발생하면 HttpClientErrorException 이 던져진다.")
    @Test
    void notFoundExceptionOnAccessTokenProviderTest() throws JsonProcessingException {
        // given
        RestTemplate restTemplate = new RestTemplate(List.of(new FormHttpMessageConverter(), new OAuth2AccessTokenResponseHttpMessageConverter()));
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();

        RestTemplateResponseClient restTemplateResponseClient = new RestTemplateResponseClient(restTemplate);
        OAuth2AccessTokenProvider oAuth2AccessTokenProvider = new OAuth2AccessTokenProvider(restTemplateResponseClient);
        ClientRegistration clientRegistration = ClientRegistrationFactory.createClientRegistration("clientRegistration");

        Map<String, String> responseMap = Map.of(
                "access_token", "accessTokenValue",
                "expires_in", "3600",
                "scope", "email, profile",
                "token_type", "Bearer",
                "id_token", "idTokenValue"
        );
        String responseJson = objectMapper.writeValueAsString(responseMap);

        server
                .expect(requestTo(clientRegistration.getProviderDetails().getTokenUri()))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", notNullValue()))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        // expect
        assertThatThrownBy(() -> oAuth2AccessTokenProvider.getAccessToken("code", clientRegistration))
                .isInstanceOf(OAuth2ClientResponseException.class);
    }

    @DisplayName("AccessTokenProvider 에서 서버 에러가 발생하면 HttpServerErrorException 이 던져진다.")
    @Test
    void serverErrorOnAccessTokenProviderTest() throws JsonProcessingException {
        // given
        RestTemplate restTemplate = new RestTemplate(List.of(new FormHttpMessageConverter(), new OAuth2AccessTokenResponseHttpMessageConverter()));
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();

        RestTemplateResponseClient restTemplateResponseClient = new RestTemplateResponseClient(restTemplate);
        OAuth2AccessTokenProvider oAuth2AccessTokenProvider = new OAuth2AccessTokenProvider(restTemplateResponseClient);
        ClientRegistration clientRegistration = ClientRegistrationFactory.createClientRegistration("clientRegistration");

        Map<String, String> responseMap = Map.of(
                "access_token", "accessTokenValue",
                "expires_in", "3600",
                "scope", "email, profile",
                "token_type", "Bearer",
                "id_token", "idTokenValue"
        );
        String responseJson = objectMapper.writeValueAsString(responseMap);

        server
                .expect(requestTo(clientRegistration.getProviderDetails().getTokenUri()))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", notNullValue()))
                .andRespond(withServerError());

        // expect
        assertThatThrownBy(() -> oAuth2AccessTokenProvider.getAccessToken("code", clientRegistration))
                .isInstanceOf(OAuth2ClientResponseException.class);
    }
}
