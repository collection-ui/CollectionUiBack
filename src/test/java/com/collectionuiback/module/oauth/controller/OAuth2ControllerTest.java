package com.collectionuiback.module.oauth.controller;

import com.collectionuiback.module.account.Account;
import com.collectionuiback.module.account.AccountRepository;
import com.collectionuiback.module.account.AccountRole;
import com.collectionuiback.module.oauth.ClientRegistrationFactory;
import com.collectionuiback.module.oauth.OAuth2Attributes;
import com.collectionuiback.module.oauth.client.OAuth2AccessTokenProvider;
import com.collectionuiback.module.oauth.client.OAuth2UserInfoProvider;
import com.collectionuiback.module.oauth.controller.dto.RequestLoginByCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class OAuth2ControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ClientRegistrationRepository clientRegistrationRepository;

    @MockBean
    StringKeyGenerator stringKeyGenerator;

    @MockBean
    OAuth2UserInfoProvider oAuth2UserInfoProvider;

    @MockBean
    OAuth2AccessTokenProvider oAuth2AccessTokenProvider;

    @Autowired
    AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        when(clientRegistrationRepository.findByRegistrationId("registrationId"))
                .thenReturn(ClientRegistrationFactory.createClientRegistration("registrationId"));

        when(stringKeyGenerator.generateKey())
                .thenReturn("stringKey");
    }

    @DisplayName("AuthorizationUri 로 요청시 로그인을 위한 경로가 리턴된다.")
    @Test
    void authorizationUriTest() throws Exception {
        mockMvc.perform(get("/oauth2/{registrationId}/authorization-uri", "registrationId"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.authorizationUri").exists())
                .andExpect(jsonPath("$.data.authorizationUri").value(authorizationUriMatcher()));
    }

    private BaseMatcher<String> authorizationUriMatcher() {
        return new BaseMatcher<>() {
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
        };
    }

    @DisplayName("지원하지 않는 RegistrationId 로 요청이 오면 BadRequest 를 반환한다.")
    @Test
    void unsupportedRegistrationIdTest() throws Exception {
        mockMvc.perform(get("/oauth2/{registrationId}/authorization-uri", "unsupportedRegistrationId"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Client registration not found: unsupportedRegistrationId"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("GrantType 이 Code 가 아닌 경우 ServerError 를 반환한다.")
    @Test
    void unsupportedGrantTypeTest() throws Exception {
        when(clientRegistrationRepository.findByRegistrationId("registrationId"))
                .thenReturn(ClientRegistrationFactory.createClientRegistrationWithAuthorizationGrantType("registrationId", AuthorizationGrantType.CLIENT_CREDENTIALS));

        mockMvc.perform(get("/oauth2/{registrationId}/authorization-uri", "registrationId"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("Unsupported grant type: client_credentials"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("정상적으로 로그인이 처리된 경우")
    @Test
    void successiveLoginTest() throws Exception {
        // given
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("registrationId");
        when(oAuth2AccessTokenProvider.getAccessToken("code", clientRegistration))
                .thenReturn(OAuth2AccessTokenResponse.withToken("accessTokenValue")
                        .tokenType(OAuth2AccessToken.TokenType.BEARER).build());

        when(oAuth2UserInfoProvider.getUserInfo("accessTokenValue", clientRegistration))
                .thenReturn(new OAuth2Attributes(Map.of(
                        "name", "nameValue",
                        "email", "email@email.com",
                        "picture", "pictureValue"
                ), "sub", "nameValue", "email@email.com", "pictureValue"));

        RequestLoginByCode requestLoginByCode = new RequestLoginByCode("code");
        String requestJson = objectMapper.writeValueAsString(requestLoginByCode);

        mockMvc.perform(post("/oauth2/{registrationId}/login", "registrationId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.email").value("email@email.com"))
                .andExpect(jsonPath("$.data.picture").value("pictureValue"))
                .andExpect(jsonPath("$.data.name").value("nameValue"))
                .andExpect(authenticated());
    }

    @DisplayName("최초로 로그인을 시도한 계정의 경우 회원가입이 수행된다.")
    @Test
    void saveAccountWhenFirstLoginTest() throws Exception {
        // given
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("registrationId");
        when(oAuth2AccessTokenProvider.getAccessToken("code", clientRegistration))
                .thenReturn(OAuth2AccessTokenResponse.withToken("accessTokenValue")
                        .tokenType(OAuth2AccessToken.TokenType.BEARER).build());

        when(oAuth2UserInfoProvider.getUserInfo("accessTokenValue", clientRegistration))
                .thenReturn(new OAuth2Attributes(Map.of(
                        "name", "nameValue",
                        "email", "email@email.com",
                        "picture", "pictureValue"
                ), "sub", "nameValue", "email@email.com", "pictureValue"));

        RequestLoginByCode requestLoginByCode = new RequestLoginByCode("code");
        String requestJson = objectMapper.writeValueAsString(requestLoginByCode);

        mockMvc.perform(post("/oauth2/{registrationId}/login", "registrationId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.email").value("email@email.com"))
                .andExpect(jsonPath("$.data.picture").value("pictureValue"))
                .andExpect(jsonPath("$.data.name").value("nameValue"))
                .andExpect(authenticated());

        Optional<Account> optionalAccount = accountRepository.findByEmail("email@email.com");
        assertThat(optionalAccount).isPresent();
        Account account = optionalAccount.get();

        assertThat(account).isNotNull();
        assertThat(account.getEmail()).isEqualTo("email@email.com");
        assertThat(account.getPicture()).isEqualTo("pictureValue");
        assertThat(account.getName()).isEqualTo("nameValue");
        assertThat(account.getPassword()).isNull();
    }

    @DisplayName("로그인시 전달받은 name, picture 값이 다르다면 업데이트 된다.")
    @Test
    void updateAccountInfoWhenDifferentResourceTest() throws Exception {
        // given
        Account account = Account.builder()
                .email("email@email.com")
                .name("nameValue")
                .picture("pictureValue")
                .role(AccountRole.GUEST)
                .build();
        accountRepository.save(account);

        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("registrationId");
        when(oAuth2AccessTokenProvider.getAccessToken("code", clientRegistration))
                .thenReturn(OAuth2AccessTokenResponse.withToken("accessTokenValue")
                        .tokenType(OAuth2AccessToken.TokenType.BEARER).build());

        when(oAuth2UserInfoProvider.getUserInfo("accessTokenValue", clientRegistration))
                .thenReturn(new OAuth2Attributes(Map.of(
                        "name", "diffName",
                        "email", "email@email.com",
                        "picture", "diffPicture"
                ), "sub", "diffName", "email@email.com", "diffPicture"));

        RequestLoginByCode requestLoginByCode = new RequestLoginByCode("code");
        String requestJson = objectMapper.writeValueAsString(requestLoginByCode);

        mockMvc.perform(post("/oauth2/{registrationId}/login", "registrationId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.email").value("email@email.com"))
                .andExpect(jsonPath("$.data.picture").value("diffPicture"))
                .andExpect(jsonPath("$.data.name").value("diffName"))
                .andExpect(authenticated());

        Optional<Account> optionalAccount = accountRepository.findByEmail("email@email.com");
        assertThat(optionalAccount).isPresent();
        Account persistedAccount = optionalAccount.get();

        assertThat(persistedAccount).isNotNull();
        assertThat(persistedAccount.getEmail()).isEqualTo("email@email.com");
        assertThat(persistedAccount.getPicture()).isEqualTo("diffPicture");
        assertThat(persistedAccount.getName()).isEqualTo("diffName");
        assertThat(persistedAccount.getPassword()).isNull();
    }

    @DisplayName("지원하지 않는 RegistrationId 로 로그인을 시도하는 경우 BadRequest 를 반환한다.")
    @Test
    void unsupportedRegistrationIdOnLoginTest() throws Exception {
        RequestLoginByCode requestLoginByCode = new RequestLoginByCode("code");
        String requestJson = objectMapper.writeValueAsString(requestLoginByCode);

        mockMvc.perform(post("/oauth2/{registrationId}/login", "unsupportedRegistrationId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Client registration not found: unsupportedRegistrationId"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
