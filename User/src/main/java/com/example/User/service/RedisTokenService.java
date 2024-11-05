package com.example.User.service;

import com.example.User.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisTokenService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final JWTUtil jwtUtil;

    public void setValues(String key, String value, Duration duration) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, value, duration);
    }

    public String getValue(String key) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        if (values.get(key) == null)
            return "none";
        return String.valueOf(values.get(key));
    }
    public String checkRefreshToken(String key) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        if (values.get(key) == null)
            throw new RuntimeException();
        String refreshToken =(String)values.get(key);
        Map<String, Object>  claims= jwtUtil.validateToken(refreshToken);

        String email = "";
        Long id = null;

        try {
            String[] s = jwtUtil.decrypt(refreshToken).split(",");
            email = s[1];
            id = Long.valueOf(s[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(!email.equals(key))
            throw new RuntimeException();

        String accessTokenValue = jwtUtil.generateToken(Map.of("id", id, "email", email), 1);

        Integer exp = (Integer) claims.get("exp");
        Date expTime = new Date(Instant.ofEpochMilli(exp).toEpochMilli() * 1000);
        Date current = new Date(System.currentTimeMillis());
        long gapTime = (expTime.getTime() - current.getTime());

        //RefrshToken이 3일도 안남았다면..
        if (gapTime < (1000 * 60 * 60)) {
            //if(gapTime < (1000 * 60 * 60 * 24 * 3  ) ){
            log.info("new Refresh Token required...  ");
            String userRefreshToken = jwtUtil.generateToken(Map.of("id", id, "email", email), 30);
            setValues(email, userRefreshToken, Duration.ofDays(1));
        }
        return accessTokenValue;
    }

}
