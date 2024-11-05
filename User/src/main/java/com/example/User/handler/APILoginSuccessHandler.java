package com.example.User.handler;

import com.example.User.service.RedisTokenService;
import com.example.User.util.JWTUtil;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class APILoginSuccessHandler  {

    private final JWTUtil jwtUtil;
    private final RedisTokenService tokenService;

    //request가 아마도 이메일
    public String onAuthenticationSuccess (String email, int id) {

        //이게 아마도 줄 데이터 이메일

        log.info("Login Success Handler................................");
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        CustomUserDetail details = (CustomUserDetail) authentication.getPrincipal();
//        log.info(details.getEmail());

        Map<String, Object> claim = Map.of("email", email);
//        log.info("email" + authentication.getName());
        //Access Token 유효기간 1일
        String accessToken = jwtUtil.generateToken(claim, 1);
        //Refresh Token 유효기간 30일
        String refreshToken = jwtUtil.generateToken(claim, 10);

        tokenService.setValues(email, refreshToken, Duration.ofDays(1));
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        Gson gson = new Gson();
//        String jsonStr = gson.toJson(Map.of("accessToken", accessToken));
//        response.getWriter().println(jsonStr);
        return accessToken;

    }
}
