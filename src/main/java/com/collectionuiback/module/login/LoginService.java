package com.collectionuiback.module.login;

import com.collectionuiback.module.account.Account;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LoginService {

    private final SecurityContextRepository securityContextRepository;

    public void persistUserInfoToSecurityContext(Account account, HttpServletRequest request, HttpServletResponse response) {
        UserDetails userDetails = CustomUserDetails.builder()
                .email(account.getEmail())
                .role(account.getRole())
                .build();

        Authentication authenticationToken = new CustomAuthenticationToken(userDetails);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authenticationToken);
        securityContextRepository.saveContext(context, request, response);
    }
}
