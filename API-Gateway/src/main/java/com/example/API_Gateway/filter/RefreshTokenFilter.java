package com.example.API_Gateway.filter;


import com.example.API_Gateway.exception.RefreshTokenException;
import com.example.API_Gateway.util.JWTUtil;
import com.google.gson.Gson;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Log4j2
@Component
public class RefreshTokenFilter extends AbstractGatewayFilterFactory<RefreshTokenFilter.Config> {

    private final JWTUtil jwtUtil;

    public RefreshTokenFilter(JWTUtil jwtUtil) {
        super(RefreshTokenFilter.Config.class);  // 반드시 필요
        this.jwtUtil = jwtUtil;
    }


    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            if (!request.getURI().getPath().equals("/refresh")) {
                log.info("skip refresh token filter.....");
                return chain.filter(exchange);
            }
            log.info("Refresh Token Filter...run..............1");

            return request.getBody()
                    .next()
                    .map(dataBuffer -> {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        return new String(bytes, StandardCharsets.UTF_8);
                    })
                    .map(this::parseRequestJSON)
                    .flatMap(tokens -> {
                        String accessToken = tokens.get("accessToken");
                        log.info("accessToken: " + accessToken);

                        try {
                            String[] text = checkAccessToken(accessToken).split(",");
                            log.info("text: " + text);
                            if (text[0].equals("retry")) {
                                String email= text[1];

                                return WebClient.create()
                                        .post()
                                        .uri("lb://User/refresh")
                                        .bodyValue(email)
                                        .retrieve()
                                        .bodyToMono(String.class)  // Map 대신 String으로 변경
                                        .flatMap(result -> {
                                            response.setStatusCode(HttpStatus.OK);
                                            return response.writeWith(
                                                    Mono.just(response.bufferFactory().wrap(
                                                            result.getBytes(StandardCharsets.UTF_8)  // 단순히 String을 byte[]로 변환
                                                    ))
                                            );
                                        })
                                        .onErrorResume(e -> onError(exchange, HttpStatus.INTERNAL_SERVER_ERROR));
                            }

                            //처리 필요
                            return chain.filter(exchange);
                        } catch (RefreshTokenException e) {
                            return onError(exchange, HttpStatus.UNAUTHORIZED);
                        }
                    });
        });
    }
    private Map<String, String> parseRequestJSON(String content) {
        return new Gson().fromJson(content, Map.class);
    }

    private String checkAccessToken(String accessToken) throws RefreshTokenException {
        Map<String, Object> values = null;
        try {
            values = jwtUtil.validateToken(accessToken);
            return (String)values.get("email");
//            return values;
        } catch (ExpiredJwtException expiredJwtException) {
            log.info("Access Token has expired");
            values = expiredJwtException.getClaims();
            String email=(String)values.get("email");
            return "retry,"+ email;
        } catch (Exception exception) {
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.NO_ACCESS);
        }
    }

    @Getter
    @Setter
    public static class Config {
        private String header = "Authorization";
        private String grant = "Bearer";
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }
    @Override
    public String name() {
        return "RefreshTokenFilter";  // yml의 필터 이름과 정확히 일치해야 함
    }

}