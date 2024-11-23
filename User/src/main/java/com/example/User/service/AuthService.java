package com.example.User.service;

import com.example.User.dto.login.TokensResponse;
import com.example.User.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {
    private final JWTUtil jwtUtil;
    private final RedisTokenService tokenService;

    public TokensResponse onAuthenticationSuccess(Integer id) {
        log.info("id {}", id);
        //원래 1일 또는 2일 으로 설정해야함
        String accessToken = jwtUtil.generateToken(id, 3);
        String refreshToken = jwtUtil.generateToken(id, 100000);

        tokenService.setValues(id, refreshToken, Duration.ofDays(1));

        return TokensResponse.of(accessToken, refreshToken);
    }
}
