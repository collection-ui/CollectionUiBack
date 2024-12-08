package com.collectionuiback.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import java.util.Base64;

@Configuration
public class SecurityConfig {

    @Bean
    public StringKeyGenerator defaultStateGenerator() {
        return new Base64StringKeyGenerator(Base64.getUrlEncoder());
    }

    @Bean
    public SecurityContextRepository delegatingSecurityContextRepository() {
        return new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository(),
                new HttpSessionSecurityContextRepository()
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                        .requestMatchers("/oauth2/{registrationId}/authorization-uri", "/oauth2/{registrationId}/login").permitAll()
                        .anyRequest().authenticated()
                );

        http
                .securityContext(securityContext -> {
                            securityContext.securityContextRepository(delegatingSecurityContextRepository());
                            securityContext.requireExplicitSave(true);
                        }
                );

        http
                .csrf(AbstractHttpConfigurer::disable);

        http.
                securityContext(securityContext -> {
                    securityContext.securityContextRepository(delegatingSecurityContextRepository());
                    securityContext.requireExplicitSave(true);
                });

        http
                .oauth2Login(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
