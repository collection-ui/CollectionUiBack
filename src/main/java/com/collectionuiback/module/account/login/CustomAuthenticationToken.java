package com.collectionuiback.module.account.login;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomAuthenticationToken extends AbstractAuthenticationToken {

    public CustomAuthenticationToken(UserDetails userDetails) {
        super(userDetails.getAuthorities());
        this.setDetails(userDetails);
        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return getDetails();
    }
}
