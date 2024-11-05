package com.example.User.util;



import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class JWTUtil {
//    @Value("${org.zerock.jwt.secret}")
    private String key="dGhpc19pc19hX3ZlcnlfbG9uZ19hbmRfc2VjdXJlX2tleV9mb3JfaHMyNTZfYWxnb3JpdGhtX2F0X2xlYXN0XzMyX2J5dGVz";
    private static final String ALGORITHM = "AES";
    private static final String FIXED_KEY = "myFixedSecretKey";
    private final SecretKey secretKey;


    public JWTUtil() {
        byte[] keyBytes = FIXED_KEY.getBytes(); // 문자열을 바이트 배열로 변환
        this.secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
    }

    public String generateToken(Map<String, Object> valueMap, int days) {

        log.info("generateKey..." + key);

        //헤더 부분
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        log.info("valueMap" + valueMap.toString());

        Map<String, Object> encrypted = null;
        String putEncrypt = valueMap.get("id") + "," + valueMap.get("email");
        try {
            encrypted = encrypt(putEncrypt);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int time = 60 * 24 * days; //테스트는 분단위로 나중에 60*24 (일)단위변경

        ZonedDateTime nowUtc = ZonedDateTime.now(ZoneId.of("UTC"));
        Date issuedAt = Date.from(nowUtc.toInstant());
        Date expiration = Date.from(nowUtc.plusMinutes(time).toInstant());

        String jwtStr = Jwts.builder()
                .setHeader(headers)
                .setClaims(encrypted)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, key.getBytes())
                .compact();

        return jwtStr;
    }

    public Map<String, Object> validateToken(String token) throws JwtException {

        Map<String, Object> claim = Jwts.parser()
                .setSigningKey(key.getBytes()) // Set Key
                .parseClaimsJws(token) // 파싱 및 검증, 실패 시 에러
                .getBody();
        return claim;
    }

    public Map<String, Object> encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        String s = Base64.getEncoder().encodeToString(encryptedBytes);
        return Map.of("payload", s);
    }

    public String decrypt(String encryptedText) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }
}