package com.example.Finance.error;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

//    @ExceptionHandler(FeignException.class)
//    private ResponseEntity<ErrorDTO> handleFeignException(FeignException e) {
//
//        return ResponseEntity
//                    .status(500)
//                    .body(ErrorDTO.builder()
//                            .code("FEIGN_ERROR")
//                            .message("금융 서버와의 통신 도중 문제가 발생했습니다")
//                            .build());
//
//    }

    @ExceptionHandler({CustomException.class})
    protected ResponseEntity<ErrorDTO> handleCustomException(CustomException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return handleExceptionInternal(errorCode);
    }

    private ResponseEntity<ErrorDTO> handleExceptionInternal(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatus())
                .body(makeErrorResponse(errorCode));
    }

    private ErrorDTO makeErrorResponse(ErrorCode errorCode) {
        return ErrorDTO.builder()
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .build();
    }
}
