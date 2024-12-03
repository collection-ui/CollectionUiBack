package com.collectionuiback.module.oauth.controller;

import com.collectionuiback.boilerplate.ResponseForm;
import com.collectionuiback.module.oauth.OAuth2Service;
import com.collectionuiback.module.oauth.ResponseUserInfo;
import com.collectionuiback.module.oauth.controller.dto.RequestLoginByCode;
import com.collectionuiback.module.oauth.controller.dto.ResponseAuthorizationUri;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/oauth2")
public class OAuth2Controller {

    private final OAuth2Service oAuth2Service;

    @GetMapping("/{registrationId}/authorization-uri")
    public ResponseEntity<ResponseForm> authorizationUri(@PathVariable("registrationId") String registrationId) {
        String authorizationUrl = oAuth2Service.getAuthorizationUrl(registrationId);

        ResponseForm responseForm = ResponseForm.success(ResponseAuthorizationUri.builder()
                .authorizationUri(authorizationUrl)
                .build());

        return ResponseEntity.ok(responseForm);
    }

    @PostMapping("/{registrationId}/login")
    public ResponseEntity<ResponseForm> login(
            @PathVariable("registrationId") String registrationId,
            @RequestBody RequestLoginByCode requestLoginByCode,
            HttpServletRequest request,
            HttpServletResponse response) {
        ResponseUserInfo responseUserInfo = oAuth2Service.loginByCode(registrationId, requestLoginByCode.getCode(), request, response);

        ResponseForm responseForm = ResponseForm.success(responseUserInfo);

        return ResponseEntity.ok(responseForm);
    }
}
