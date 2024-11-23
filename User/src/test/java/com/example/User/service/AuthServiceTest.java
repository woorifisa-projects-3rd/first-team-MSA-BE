package com.example.User.service;


import com.example.User.dto.login.TokensResponse;
import com.example.User.util.JWTUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceTest.class);
    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private RedisTokenService tokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("인증 성공시 토큰 생성 및 저장 테스트")
    public void onAuthenticationSuccess_Success() {
        log.info("onAuthenticationSuccess");
        // Given
        Integer userId = 1;
        String mockAccessToken = "mock-access-token";
        String mockRefreshToken = "mock-refresh-token";

        log.info(mockAccessToken);
        when(jwtUtil.generateToken(userId, 100)).thenReturn(mockAccessToken);
        when(jwtUtil.generateToken(userId, 1000)).thenReturn(mockRefreshToken);

        // When
        TokensResponse tokensResponse  = authService.onAuthenticationSuccess(userId);

        assertThat(tokensResponse.getAccessToken()).isEqualTo(mockAccessToken);
        // Then
        verify(jwtUtil).generateToken(userId, 100);
        verify(jwtUtil).generateToken(userId, 1000);
        verify(tokenService).setValues(eq(userId), eq(mockRefreshToken), eq(Duration.ofDays(1)));
    }
}