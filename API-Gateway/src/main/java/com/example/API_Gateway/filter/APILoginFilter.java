package com.example.API_Gateway.filter;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
public class APILoginFilter extends AbstractGatewayFilterFactory<APILoginFilter.Config> {


    public APILoginFilter() {
        super(APILoginFilter.Config.class);  // 반드시 필요=
    }
    @Override
    public String name() {
        return "APILoginFilter";  // yml의 필터 이름과 정확히 일치해야 함
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            URI uri = request.getURI();

            if (!request.getMethod().equals(HttpMethod.POST)) {
                log.info("GET METHOD NOT SUPPORT");
                return chain.filter(exchange);
            }

            if (!uri.getPath().contains("/login")) {
                return chain.filter(exchange);
            }

            log.info("APILoginFilter-----------------------------------");

            return request.getBody()
                    .next()
                    .map(dataBuffer -> {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        return new String(bytes, StandardCharsets.UTF_8);
                    })
                    .map(this::parseRequestJSON)
                    .flatMap(jsonData -> {
                        log.info("Login request data: {}", jsonData);
                        return WebClient.create()
                                .post()
                                .uri("")
                                .bodyValue(jsonData)
                                .retrieve()
                                .bodyToMono(Map.class)  // Map 대신 String으로 변경
                                .flatMap(result -> {
                                    String token = (String) result.get("accessToken");
                                    log.info("Login token: {}", token);

                                    response.setStatusCode(HttpStatus.OK);
                                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                                    response.getHeaders().add("Authorization", "Bearer " + token);

                                    return response.writeWith(
                                            Mono.just(response.bufferFactory().wrap(
                                                    new Gson().toJson(result).getBytes(StandardCharsets.UTF_8)
                                            ))
                                    );
                                })
                                .onErrorResume(e -> {
                                    log.error("Auth service error: ", e);
                                    return onError(exchange, HttpStatus.INTERNAL_SERVER_ERROR);
                                });
                    });

        };
    }


//    String token = (String) result.get("accessToken");
//    log.info("Login token: {}", token);
//
//    // 응답 설정
//    response.setStatusCode(HttpStatus.OK);
//    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
//    response.getHeaders().add("Authorization", "Bearer " + token);
//
//    // 응답 반환
//    return response.writeWith(
//            Mono.just(response.bufferFactory().wrap(
//            new Gson().toJson(result).getBytes(StandardCharsets.UTF_8)
//        ))
//                );
    //인증 시도할때 하는 메소드
    private Map<String, String> parseRequestJSON(String content) {
        return new Gson().fromJson(content, Map.class);
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

}
