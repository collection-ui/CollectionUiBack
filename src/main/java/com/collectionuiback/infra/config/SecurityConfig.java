package com.collectionuiback.infra.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
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

    @Profile("!prod")
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(PathRequest.toH2Console())
                .requestMatchers("/swagger/**")
                .requestMatchers("/swagger-ui/**")
                .requestMatchers("/v3/api-docs/**")
                .requestMatchers("/docs/*")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationEntryPoint authenticationEntryPoint, AccessDeniedHandler accessDeniedHandler) throws Exception {
        http
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                        .requestMatchers("/oauth2/{registrationId}/authorization-uri", "/oauth2/{registrationId}/login").permitAll()
                        .requestMatchers("/swagger/**", "/swagger-ui/**", "/v3/api-docs/**", "/docs/*").denyAll()
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
                .oauth2Login(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        http
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(authenticationEntryPoint)
                                .accessDeniedHandler(accessDeniedHandler));

        return http.build();
    }
}
