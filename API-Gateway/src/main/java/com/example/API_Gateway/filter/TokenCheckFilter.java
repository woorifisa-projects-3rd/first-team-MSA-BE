package com.example.API_Gateway.filter;


import com.example.API_Gateway.exception.AccessTokenException;
import com.example.API_Gateway.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;


@Slf4j
@Component
public class TokenCheckFilter extends AbstractGatewayFilterFactory<TokenCheckFilter.Config> {

    private final JWTUtil jwtUtil;

    public TokenCheckFilter(JWTUtil jwtUtil) {
        super(TokenCheckFilter.Config.class);  // 반드시 필요
        this.jwtUtil = jwtUtil;
    }
    @Override
    public String name() {
        return "TokenCheckFilter";  // yml의 필터 이름과 정확히 일치해야 함
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            String path = request.getURI().getPath();
//            if (!path.startsWith("/user/")) {
//                return chain.filter(exchange);
//            }

            log.info("Token Check Filter..........................");

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                log.info("헤더에 정보가 없습니다");
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            String authorization = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            log.info("authorization"+authorization);
            String tokenStr = authorization.replace("Bearer ", "");
            log.info("tokenStr"+tokenStr);

            try {
                Map<String, Object> payload = validateAccessToken(tokenStr);
                String email = "";
                try {
                    email = jwtUtil.decrypt((String) payload.get("payload"));
                } catch (Exception e) {
                    log.error("Decryption error", e);
                    return onError(exchange, HttpStatus.UNAUTHORIZED);
                }

                log.info("email: " + email);

                // 검증된 정보를 헤더에 추가하여 다음 서비스로 전달
                ServerHttpRequest mutatedRequest = request.mutate()
                        .header("X-Auth-Email", email)
                        .build();

                return chain.filter(exchange.mutate().request(mutatedRequest).build());

            } catch (AccessTokenException e) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }
        });
    }

    private Map<String, Object> validateAccessToken(String tokenStr) throws AccessTokenException {
        try {
            return jwtUtil.validateToken(tokenStr);
        } catch (MalformedJwtException malformedJwtException) {
            log.error("MalformedJwtException----------------------");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.MALFORM);
        } catch (SignatureException signatureException) {
            log.error("SignatureException----------------------");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BADSIGN);
        } catch (ExpiredJwtException expiredJwtException) {
            log.error("ExpiredJwtException----------------------");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.EXPIRED);
        }
    }

    @Getter
    @Setter
    public static class Config {
        private String header = "Authorization";
        private String grant = "Bearer";
    }

    private ServerHttpRequest addAuthorization(ServerHttpRequest request, String email) {
        URI uri = URI.create(request.getURI() + "/" + email);
        return request.mutate()
                .uri(uri)
                .build();
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }

}