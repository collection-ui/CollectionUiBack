package com.collectionuiback.module.oauth;

import com.collectionuiback.module.account.Account;
import com.collectionuiback.module.account.AccountService;
import com.collectionuiback.module.login.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OAuth2Service {

    private final OAuth2AuthorizationProvider oAuth2AuthorizationProvider;
    private final AccountService accountService;
    private final LoginService loginService;

    public String getAuthorizationUrl(String registrationId) {
        return oAuth2AuthorizationProvider.resolveAuthorizationRequestUri(registrationId);
    }

    public ResponseUserInfo loginByCode(String registrationId, String code, HttpServletRequest request, HttpServletResponse response) {
        OAuth2Attributes oAuth2Attributes = oAuth2AuthorizationProvider.loadAuthorizationByCode(registrationId, code);

        Account account = accountService.saveOrUpdate(oAuth2Attributes.toEntity());

        loginService.persistUserInfoToSecurityContext(account, request, response);

        return ResponseUserInfo.builder()
                .email(account.getEmail())
                .name(account.getName())
                .picture(account.getPicture())
                .role(account.getRoleName())
                .build();
    }

}
