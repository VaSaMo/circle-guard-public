package com.circleguard.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class AuthTokenUtilsTest {

    private JwtTokenService jwtTokenService;

    @BeforeEach
    void setUp() {
        jwtTokenService = new JwtTokenService("secretsecretsecretsecretsecretsecretsecretsecret", 3600000);
    }

    @Test
    void shouldGenerateValidToken() {
        UUID anonymousId = UUID.randomUUID();
        Authentication auth = mock(Authentication.class);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
            .when(auth).getAuthorities();

        String token = jwtTokenService.generateToken(anonymousId, auth);

        assertNotNull(token);
    }
}
