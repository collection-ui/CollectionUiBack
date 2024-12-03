package com.collectionuiback.module.oauth.client;

import com.collectionuiback.infra.client.RestTemplateResponseClient;
import com.collectionuiback.module.oauth.ClientRegistrationFactory;
import com.collectionuiback.module.oauth.OAuth2Attributes;
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
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest
class OAuth2UserInfoProviderTest {

    @Autowired
    ObjectMapper objectMapper;

    @DisplayName("UserInfo 경로로 AccessToken 을 담아 요청하면 User 정보를 반환받는다. UserInfoAuthenticationMethod 가 Header 인 경우 Get 으로 요청을 전송한다.")
    @Test
    void requestUserInfoEndpointWithAccessToken() throws JsonProcessingException {
        // given
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();

        RestTemplateResponseClient restTemplateResponseClient = new RestTemplateResponseClient(restTemplate);
        OAuth2UserInfoProvider oAuth2UserInfoProvider = new OAuth2UserInfoProvider(restTemplateResponseClient);
        ClientRegistration clientRegistration = ClientRegistrationFactory.createClientRegistration("clientRegistration");

        // when
        Map<String, String> responseMap = Map.of(
                "name", "nameValue",
                "email", "email@email.com",
                "picture", "pictureValue"
        );
        String responseJson = objectMapper.writeValueAsString(responseMap);

        server
                .expect(requestTo(clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri()))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer " + "accessTokenValue"))
                .andRespond(MockRestResponseCreators.withSuccess(responseJson, MediaType.APPLICATION_JSON));

        OAuth2Attributes oAuth2Attributes = oAuth2UserInfoProvider.getUserInfo("accessTokenValue", clientRegistration);

        // then
        assertThat(oAuth2Attributes).isNotNull();
        assertThat(oAuth2Attributes.getName()).isEqualTo("nameValue");
        assertThat(oAuth2Attributes.getEmail()).isEqualTo("email@email.com");
        assertThat(oAuth2Attributes.getPicture()).isEqualTo("pictureValue");
        assertThat(oAuth2Attributes.getNameAttributeKey()).isEqualTo(IdTokenClaimNames.SUB);
    }

    @DisplayName("UserInfo 경로로 AccessToken 을 담아 요청하면 User 정보를 반환받는다. UserInfoAuthenticationMethod 가 Form 인 경우 Post 으로 요청을 전송한다.")
    @Test
    void requestUserInfoEndpointWithAccessTokenWithFormAuthenticationMethod() throws JsonProcessingException {
        // given
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();

        RestTemplateResponseClient restTemplateResponseClient = new RestTemplateResponseClient(restTemplate);
        OAuth2UserInfoProvider oAuth2UserInfoProvider = new OAuth2UserInfoProvider(restTemplateResponseClient);
        ClientRegistration clientRegistration = ClientRegistrationFactory.createClientRegistrationWithFormUserInfoAuthenticationMethod("clientRegistration");

        // when
        Map<String, String> responseMap = Map.of(
                "name", "nameValue",
                "email", "email@email.com",
                "picture", "pictureValue"
        );
        String responseJson = objectMapper.writeValueAsString(responseMap);

        server
                .expect(requestTo(clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri()))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_FORM_URLENCODED + ";charset=UTF-8"))
                .andExpect(content().formDataContains(Map.of(OAuth2ParameterNames.ACCESS_TOKEN, "accessTokenValue")))
                .andRespond(MockRestResponseCreators.withSuccess(responseJson, MediaType.APPLICATION_JSON));

        OAuth2Attributes oAuth2Attributes = oAuth2UserInfoProvider.getUserInfo("accessTokenValue", clientRegistration);

        // then
        assertThat(oAuth2Attributes).isNotNull();
        assertThat(oAuth2Attributes.getName()).isEqualTo("nameValue");
        assertThat(oAuth2Attributes.getEmail()).isEqualTo("email@email.com");
        assertThat(oAuth2Attributes.getPicture()).isEqualTo("pictureValue");
        assertThat(oAuth2Attributes.getNameAttributeKey()).isEqualTo(IdTokenClaimNames.SUB);
    }

    @DisplayName("UserInfoProvider 에서 404 에러가 발생하면 HttpClientErrorException 이 던져진다.")
    @Test
    void notFoundExceptionOnAccessTokenProviderTest() throws JsonProcessingException {
        // given
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();

        RestTemplateResponseClient restTemplateResponseClient = new RestTemplateResponseClient(restTemplate);
        OAuth2UserInfoProvider oAuth2UserInfoProvider = new OAuth2UserInfoProvider(restTemplateResponseClient);
        ClientRegistration clientRegistration = ClientRegistrationFactory.createClientRegistration("clientRegistration");

        // when
        Map<String, String> responseMap = Map.of(
                "name", "nameValue",
                "email", "email@email.com",
                "picture", "pictureValue"
        );
        String responseJson = objectMapper.writeValueAsString(responseMap);

        server
                .expect(requestTo(clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri()))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer " + "accessTokenValue"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        // expect
        assertThatThrownBy(() -> oAuth2UserInfoProvider.getUserInfo("accessTokenValue", clientRegistration))
                .isInstanceOf(OAuth2ClientResponseException.class);
    }

    @DisplayName("UserInfoProvider 에서 서버 에러가 발생하면 HttpServerErrorException 이 던져진다.")
    @Test
    void serverErrorOnAccessTokenProviderTest() throws JsonProcessingException {
        // given
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();

        RestTemplateResponseClient restTemplateResponseClient = new RestTemplateResponseClient(restTemplate);
        OAuth2UserInfoProvider oAuth2UserInfoProvider = new OAuth2UserInfoProvider(restTemplateResponseClient);
        ClientRegistration clientRegistration = ClientRegistrationFactory.createClientRegistration("clientRegistration");

        // when
        Map<String, String> responseMap = Map.of(
                "name", "nameValue",
                "email", "email@email.com",
                "picture", "pictureValue"
        );
        String responseJson = objectMapper.writeValueAsString(responseMap);

        server
                .expect(requestTo(clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri()))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer " + "accessTokenValue"))
                .andRespond(withServerError());

        // expect
        assertThatThrownBy(() -> oAuth2UserInfoProvider.getUserInfo("accessTokenValue", clientRegistration))
                .isInstanceOf(OAuth2ClientResponseException.class);
    }
}
