package com.example.User.service;


import com.example.User.dto.login.TokensResponse;
import com.example.User.error.CustomException;
import com.example.User.error.ErrorCode;
import com.example.User.util.CryptoUtil;
import com.example.User.util.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOps;

    @Mock
    private CryptoUtil cryptoUtil;

    @Mock
    private JWTUtil jwtUtil;

    @InjectMocks
    private RedisTokenService redisTokenService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);

        // RedisTokenService를 직접 생성해서 valueOps 설정
        redisTokenService = new RedisTokenService(redisTemplate, jwtUtil,cryptoUtil);
    }

    @Test
    @DisplayName("리프레시 토큰 검증 성공 테스트+checkAndRenewRefreshToken는 안함")
    void checkRefreshToken_Success() {
        // Given
        Integer accessTokenId = 1;
        String refreshToken = "refresh-token";
        String encryptedId = "encrypted-1";
        int currentTimeSeconds = (int)(System.currentTimeMillis() / 1000);
        int expirationTime = currentTimeSeconds + (30 * 60*50);  //시간 충분할때
        Map<String, Object> claims = new HashMap<>();
        claims.put("payload", encryptedId);
        claims.put("exp", expirationTime);

        when(valueOps.get(accessTokenId.toString())).thenReturn(refreshToken);

        when(jwtUtil.validateToken(refreshToken)).thenReturn(claims);
        when(cryptoUtil.decrypt(encryptedId)).thenReturn(accessTokenId);
        when(jwtUtil.generateToken(accessTokenId, 1)).thenReturn("new-access-token");

        // When
        TokensResponse tokensResponse = redisTokenService.checkRefreshToken(accessTokenId);



        // Then
        assertThat(tokensResponse.getAccessToken()).isEqualTo("new-access-token");

        verify(valueOps).get(accessTokenId.toString());


        verify(jwtUtil).validateToken(refreshToken);
        verify(cryptoUtil).decrypt(encryptedId);
        verify(jwtUtil).generateToken(accessTokenId, 1);

    }

    @Test
    @DisplayName("리프레시 토큰이 없을 때 예외 발생")
    void checkRefreshToken_EmptyToken() {
        // Given
        Integer accessTokenId = 1;
        when(valueOps.get(accessTokenId.toString())).thenReturn(null);

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () ->
                redisTokenService.checkRefreshToken(accessTokenId)
        );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EMPTY_REFRESH_TOKEN);
    }

    @Test
    @DisplayName("ID가 일치하지 않을 때 예외 발생")
    void checkRefreshToken_InvalidToken() {
        // Given
        Integer accessTokenId = 1;
        String refreshToken = "refresh-token";
        String encryptedId = "encrypted-2"; // 다른 ID로 암호화됨
        Map<String, Object> claims = new HashMap<>();
        claims.put("payload", encryptedId);
        claims.put("exp", (int)(System.currentTimeMillis() / 1000) + 3600);

        when(valueOps.get(accessTokenId.toString())).thenReturn(refreshToken);
        when(jwtUtil.validateToken(refreshToken)).thenReturn(claims);
        when(cryptoUtil.decrypt(encryptedId)).thenReturn(2); // 다른 ID 반환

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () ->
                redisTokenService.checkRefreshToken(accessTokenId)
        );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_REFRESH_TOKEN);
    }

    @Test
    @DisplayName("리프레시 토큰 갱신 테스트 - 만료 시간이 1시간 미만 남음")
    void checkAndRenewRefreshToken_ShouldRenew() {
        // Given
        Integer id = 1;
        int expirationTime = (int)(System.currentTimeMillis() / 1000) + 30 * 60; // 30분 후 만료

        String newRefreshToken = "new-refresh-token";
        when(jwtUtil.generateToken(id, 30)).thenReturn(newRefreshToken);

        // When
        redisTokenService.checkAndRenewRefreshToken(id, expirationTime);

        // Then
        verify(jwtUtil).generateToken(id, 30);
        verify(valueOps).set(
                eq(id.toString()),
                eq(newRefreshToken),
                eq(Duration.ofDays(100))
        );
    }

    @Test
    @DisplayName("리프레시 토큰 갱신 테스트 - 만료 시간이 충분히 남음")
    void checkAndRenewRefreshToken_ShouldNotRenew() {
        // Given
        Integer id = 1;
        int expirationTime = (int)(System.currentTimeMillis() / 1000) + 24 * 3600; // 24시간 후 만료

        // When
        redisTokenService.checkAndRenewRefreshToken(id, expirationTime);

        // Then
        verify(jwtUtil, never()).generateToken(any(), anyInt());
        verify(valueOps, never()).set(any(), any(), any(Duration.class));
    }

    @Test
    @DisplayName("리프레시 토큰 삭제 성공 테스트 - 키가 존재하는 경우")
    void removeRefreshToken_WhenKeyExists() {
        // Given
        Integer id = 1;
        when(redisTemplate.hasKey(id.toString())).thenReturn(true);

        // When
        redisTokenService.removeRefreshToken(id);

        // Then
        verify(redisTemplate).hasKey(id.toString());
        verify(redisTemplate).delete(id.toString());
    }

    @Test
    @DisplayName("리프레시 토큰 삭제 테스트 - 키가 존재하지 않는 경우")
    void removeRefreshToken_WhenKeyNotExists() {
        // Given
        Integer id = 1;
        when(redisTemplate.hasKey(id.toString())).thenReturn(false);

        // When
        redisTokenService.removeRefreshToken(id);

        // Then
        verify(redisTemplate).hasKey(id.toString());
        verify(redisTemplate, never()).delete(anyString());
    }

    @Test
    @DisplayName("리프레시 토큰 저장 테스트")
    void setValues_Success() {
        // Given
        Integer id = 1;
        String value = "test-refresh-token";
        Duration duration = Duration.ofDays(100);

        // When
        redisTokenService.setValues(id, value, duration);

        // Then
        verify(redisTemplate.opsForValue()).set(
                eq(id.toString()),
                eq(value),
                eq(duration)
        );
    }
}