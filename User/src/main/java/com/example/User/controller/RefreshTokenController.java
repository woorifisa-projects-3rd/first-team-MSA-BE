package com.example.User.controller;

import com.example.User.dto.ResNewAccessToken;
import com.example.User.handler.APILoginSuccessHandler;
import com.example.User.service.RedisTokenService;
import com.example.User.util.JWTUtil;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


import java.io.IOException;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenController {
    private final RedisTokenService redisTokenService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final APILoginSuccessHandler apiLoginSuccessHandler;

    @GetMapping("/refresh")
    ResponseEntity<ResNewAccessToken> refreshToken(String email) {

        String accessTokenValue =redisTokenService.checkRefreshToken(email);

        ResNewAccessToken resNewAccessToken=ResNewAccessToken.builder()
                .accessToken(accessTokenValue)
                .build();
        return ResponseEntity.ok(resNewAccessToken);
    }

    @PostMapping("/login")
    ResponseEntity<ResNewAccessToken> login(Map<String,Object> json, HttpServletResponse response) throws IOException {

        String email= (String) json.get("email");
        String password= (String) json.get("password");
        String testEmail = "test1@gmail.com";
        log.info("email "+email);
        String accessToken = apiLoginSuccessHandler.onAuthenticationSuccess(testEmail, 1);
        ResNewAccessToken resNewAccessToken=ResNewAccessToken.builder()
                .accessToken(accessToken)
                .build();
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        Gson gson = new Gson();
//        String jsonStr = gson.toJson(Map.of("accessToken", accessToken));
//        response.getWriter().println(jsonStr);
        return ResponseEntity.ok(resNewAccessToken);
    }
    @GetMapping("/test10")
    ResponseEntity<String> test() {
        return ResponseEntity.ok("Hello World");
    }
}
