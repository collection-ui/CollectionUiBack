package com.collectionuiback.module.oauth.controller;

import com.collectionuiback.module.oauth.ClientRegistrationFactory;
import com.collectionuiback.module.oauth.OAuth2Attributes;
import com.collectionuiback.module.oauth.client.OAuth2AccessTokenDto;
import com.collectionuiback.module.oauth.client.OAuth2AccessTokenProvider;
import com.collectionuiback.module.oauth.client.OAuth2UserInfoProvider;
import com.collectionuiback.module.oauth.controller.dto.RequestLoginByCode;
import com.collectionuiback.module.oauth.exception.OAuth2ClientResponseException;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@ExtendWith(RestDocumentationExtension.class)
public class RestDocOAuth2ControllerTest {

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

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation)
                        .uris()
                        .withScheme("collection-ui.com")
                        .withPort(443)
                        .and()
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();

        when(clientRegistrationRepository.findByRegistrationId("registrationId"))
                .thenReturn(ClientRegistrationFactory.createClientRegistration("registrationId"));

        when(stringKeyGenerator.generateKey())
                .thenReturn("stringKey");
    }

    @DisplayName("[RestDocs] GetAuthorizationUriTest")
    @Test
    void getAuthorizationUriTest() throws Exception {
        mockMvc.perform(get("/oauth2/{registrationId}/authorization-uri", "registrationId"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.authorizationUri").exists())
                .andExpect(jsonPath("$.data.authorizationUri").value(new AuthorizationUriMatcher()))
                .andDo(document(
                        "GetAuthorizationUri",
                        pathParameters(
                                RequestDocumentation.parameterWithName("registrationId").description("OAuth2 Platform Name")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("요청 결과 코드"),
                                fieldWithPath("data").description("요청 결과"),
                                fieldWithPath("data.authorizationUri").description("로그인 경로")
                        ),
                        resource(ResourceSnippetParameters.builder()
                                .tag("OAuth2")
                                .description("로그인 경로 요청")
                                .pathParameters(parameterWithName("registrationId").description("OAuth2 Platform Name"))
                                .responseSchema(Schema.schema("ResponseAuthorizationUrl"))
                                .responseFields(
                                        fieldWithPath("statusCode").description("요청 결과 코드"),
                                        fieldWithPath("data").description("요청 결과"),
                                        fieldWithPath("data.authorizationUri").description("로그인 경로")
                                )
                                .build())
                ));
    }

    @DisplayName("[RestDocs] GetAuthorizationUriTest WrongRegistrationId")
    @Test
    void unsupportedRegistrationIdTest() throws Exception {
        mockMvc.perform(get("/oauth2/{registrationId}/authorization-uri", "unsupportedRegistrationId"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value("Client registration not found: unsupportedRegistrationId"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(document(
                        "GetAuthorizationUri-WrongRegistrationId",
                        pathParameters(
                                RequestDocumentation.parameterWithName("registrationId").description("OAuth2 Platform Name")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("요청 결과 코드"),
                                fieldWithPath("data").description("요청 결과"),
                                fieldWithPath("message").description("에러 정보")
                        ),
                        resource(ResourceSnippetParameters.builder()
                                .tag("OAuth2")
                                .description("로그인 경로 요청")
                                .pathParameters(parameterWithName("registrationId").description("OAuth2 Platform Name"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .responseFields(
                                        fieldWithPath("statusCode").description("요청 결과 코드"),
                                        fieldWithPath("data").description("요청 결과"),
                                        fieldWithPath("message").description("에러 정보")
                                )
                                .build())
                ));
    }

    @DisplayName("[RestDocs] GetAuthorizationUrlTest UnsupportedGrantType")
    @Test
    void unsupportedGrantTypeTest() throws Exception {
        when(clientRegistrationRepository.findByRegistrationId("registrationId"))
                .thenReturn(ClientRegistrationFactory.createClientRegistrationWithAuthorizationGrantType("registrationId", AuthorizationGrantType.CLIENT_CREDENTIALS));

        mockMvc.perform(get("/oauth2/{registrationId}/authorization-uri", "registrationId"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value(500))
                .andExpect(jsonPath("$.message").value("Unsupported grant type: client_credentials"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(document(
                        "GetAuthorizationUri-UnsupportedGrantType",
                        pathParameters(
                                RequestDocumentation.parameterWithName("registrationId").description("OAuth2 Platform Name")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("요청 결과 코드"),
                                fieldWithPath("data").description("요청 결과"),
                                fieldWithPath("message").description("에러 정보")
                        ),
                        resource(ResourceSnippetParameters.builder()
                                .tag("OAuth2")
                                .description("로그인 경로 요청")
                                .pathParameters(parameterWithName("registrationId").description("OAuth2 Platform Name"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .responseFields(
                                        fieldWithPath("statusCode").description("요청 결과 코드"),
                                        fieldWithPath("data").description("요청 결과"),
                                        fieldWithPath("message").description("에러 정보")
                                )
                                .build())
                ));
    }

    @DisplayName("[RestDocs] OAuth2 Login Test")
    @Test
    void oAuth2LoginTest() throws Exception {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("registrationId");
        when(oAuth2AccessTokenProvider.getAccessToken("code", clientRegistration))
                .thenReturn(OAuth2AccessTokenDto.builder().accessToken("accessTokenValue").build());

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
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.email").value("email@email.com"))
                .andExpect(jsonPath("$.data.picture").value("pictureValue"))
                .andExpect(jsonPath("$.data.name").value("nameValue"))
                .andExpect(authenticated())
                .andDo(document(
                        "OAuth2Login",
                        pathParameters(
                                RequestDocumentation.parameterWithName("registrationId").description("OAuth2 Platform Name")
                        ),
                        requestFields(
                                fieldWithPath("code").description("OAuth2 Login Redirect 결과 코드값")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("요청 결과 코드"),
                                fieldWithPath("data").description("요청 결과"),
                                fieldWithPath("data.email").description("로그인된 유저의 이메일"),
                                fieldWithPath("data.picture").description("로그인된 유저의 프로필사진"),
                                fieldWithPath("data.name").description("로그인된 유저의 이름"),
                                fieldWithPath("data.role").description("로그인된 유저의 권한")
                        ),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("OAuth2")
                                        .description("로그인 요청")
                                        .pathParameters(parameterWithName("registrationId").description("OAuth2 Platform Name"))
                                        .requestSchema(Schema.schema("RequestLoginByCode"))
                                        .requestFields(
                                                fieldWithPath("code").description("OAuth2 Login Redirect 결과 코드값")
                                        )
                                        .responseSchema(Schema.schema("ResponseUserInfo"))
                                        .responseFields(
                                                fieldWithPath("statusCode").description("요청 결과 코드"),
                                                fieldWithPath("data").description("요청 결과"),
                                                fieldWithPath("data.email").description("로그인된 유저의 이메일"),
                                                fieldWithPath("data.picture").description("로그인된 유저의 프로필사진"),
                                                fieldWithPath("data.name").description("로그인된 유저의 이름"),
                                                fieldWithPath("data.role").description("로그인된 유저의 권한")
                                        )
                                        .build())
                ));
    }

    @DisplayName("[RestDocs] OAuth2 Login Test WrongRegistrationId")
    @Test
    void unsupportedRegistrationIdWhenLoginTest() throws Exception {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("registrationId");
        when(oAuth2AccessTokenProvider.getAccessToken("code", clientRegistration))
                .thenReturn(OAuth2AccessTokenDto.builder().accessToken("accessTokenValue").build());

        when(oAuth2UserInfoProvider.getUserInfo("accessTokenValue", clientRegistration))
                .thenReturn(new OAuth2Attributes(Map.of(
                        "name", "nameValue",
                        "email", "email@email.com",
                        "picture", "pictureValue"
                ), "sub", "nameValue", "email@email.com", "pictureValue"));

        RequestLoginByCode requestLoginByCode = new RequestLoginByCode("code");
        String requestJson = objectMapper.writeValueAsString(requestLoginByCode);

        mockMvc.perform(post("/oauth2/{registrationId}/login", "unsupportedRegistrationId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value("Client registration not found: unsupportedRegistrationId"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(unauthenticated())
                .andDo(document(
                        "OAuth2Login-WrongRegistrationId",
                        pathParameters(
                                RequestDocumentation.parameterWithName("registrationId").description("OAuth2 Platform Name")
                        ),
                        requestFields(
                                fieldWithPath("code").description("OAuth2 Login Redirect 결과 코드값")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("요청 결과 코드"),
                                fieldWithPath("data").description("요청 결과"),
                                fieldWithPath("message").description("에러 정보")
                        ),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("OAuth2")
                                        .description("로그인 요청")
                                        .pathParameters(parameterWithName("registrationId").description("OAuth2 Platform Name"))
                                        .requestSchema(Schema.schema("RequestLoginByCode"))
                                        .requestFields(
                                                fieldWithPath("code").description("OAuth2 Login Redirect 결과 코드값")
                                        )
                                        .responseSchema(Schema.schema("ErrorResponse"))
                                        .responseFields(
                                                fieldWithPath("statusCode").description("요청 결과 코드"),
                                                fieldWithPath("data").description("요청 결과"),
                                                fieldWithPath("message").description("에러 정보")
                                        )
                                        .build())
                ));
    }

    @DisplayName("[RestDocs] OAuth2 Login Test InvalidCode")
    @Test
    void invalidCodeWhenLoginTest() throws Exception {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("registrationId");
        when(oAuth2AccessTokenProvider.getAccessToken("invalidCode", clientRegistration))
                .thenThrow(new OAuth2ClientResponseException("Occurred OAuth2 Client Exception"));

        when(oAuth2UserInfoProvider.getUserInfo("accessTokenValue", clientRegistration))
                .thenReturn(new OAuth2Attributes(Map.of(
                        "name", "nameValue",
                        "email", "email@email.com",
                        "picture", "pictureValue"
                ), "sub", "nameValue", "email@email.com", "pictureValue"));

        RequestLoginByCode requestLoginByCode = new RequestLoginByCode("invalidCode");
        String requestJson = objectMapper.writeValueAsString(requestLoginByCode);

        mockMvc.perform(post("/oauth2/{registrationId}/login", "registrationId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value(500))
                .andExpect(jsonPath("$.message").value("Occurred OAuth2 Client Exception"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(unauthenticated())
                .andDo(document(
                        "OAuth2Login-InvalidCode",
                        pathParameters(
                                RequestDocumentation.parameterWithName("registrationId").description("OAuth2 Platform Name")
                        ),
                        requestFields(
                                fieldWithPath("code").description("OAuth2 Login Redirect 결과 코드값")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("요청 결과 코드"),
                                fieldWithPath("data").description("요청 결과"),
                                fieldWithPath("message").description("에러 정보")
                        ),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("OAuth2")
                                        .description("로그인 요청")
                                        .pathParameters(parameterWithName("registrationId").description("OAuth2 Platform Name"))
                                        .requestSchema(Schema.schema("RequestLoginByCode"))
                                        .requestFields(
                                                fieldWithPath("code").description("OAuth2 Login Redirect 결과 코드값")
                                        )
                                        .responseSchema(Schema.schema("ErrorResponse"))
                                        .responseFields(
                                                fieldWithPath("statusCode").description("요청 결과 코드"),
                                                fieldWithPath("data").description("요청 결과"),
                                                fieldWithPath("message").description("에러 정보")
                                        )
                                        .build())
                ));
    }
}
