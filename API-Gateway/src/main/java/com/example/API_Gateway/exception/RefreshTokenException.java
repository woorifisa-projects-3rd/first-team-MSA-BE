package com.example.API_Gateway.exception;

import com.google.gson.Gson;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class RefreshTokenException extends RuntimeException {

    private ErrorCase errorCase;

    public enum ErrorCase {
        NO_ACCESS, NO_REFRESH, OLD_REFRESH
    }

    public RefreshTokenException(ErrorCase errorCase) {
        super(errorCase.name());
        this.errorCase = errorCase;
    }

    public void sendResponseError(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        Gson gson = new Gson();

        String responseStr = gson.toJson(Map.of("msg", errorCase.name(), "time", new Date()));

        DataBuffer buffer = response.bufferFactory().wrap(responseStr.getBytes(StandardCharsets.UTF_8));
        response.writeWith(Mono.just(buffer)).doOnError(error -> DataBufferUtils.release(buffer));
    }
}