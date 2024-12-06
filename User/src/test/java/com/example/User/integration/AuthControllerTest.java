package com.example.User.integration;

import com.example.User.controller.AuthController;
import com.example.User.dto.login.ReqLoginData;
import com.example.User.dto.login.ReqRegist;
import com.example.User.dto.login.ResNewAccessToken;
import com.example.User.error.CustomException;
import com.example.User.error.ErrorCode;
import com.example.User.service.PresidentService;
import com.example.User.service.RedisTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.Duration;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static reactor.core.publisher.Mono.when;

@SpringBootTest(properties = {
})
@Transactional
class AuthControllerTest {

    @MockBean
    private RedisTokenService redisTokenService;

    @Autowired
    private AuthController authController;


    private ReqLoginData reqLoginData;

    @BeforeEach
    public void setReqLoginData(){
        reqLoginData = new ReqLoginData();  // 객체 생성
        // 또는
        // reqLoginData = ReqLoginData.builder().build();  // 빌더 패턴 사용시

        reqLoginData.setEmail("j525252p@gmail.com");
        reqLoginData.setPassword("qwer1234!");

        doNothing().when(redisTokenService)
                .setValues(any(Integer.class), anyString(), any(Duration.class));
    }

    @Test
    @DisplayName("로그인")
    public void 로그인_성공() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token123");

        ResponseEntity<ResNewAccessToken> loginresponse = authController.login(reqLoginData, request) ;

        Assertions.assertEquals(loginresponse.getStatusCode(), HttpStatus.OK);
    }

    @Test
    @DisplayName("로그인실패")
    public void 로그인_실패() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token123");


        assertThrows(CustomException.class, () ->
                authController.login(reqLoginData, request)
        );
    }

    @Test
    @DisplayName("회원가입 성공")
    public void regist_success() {
        ReqRegist reqRegist = new ReqRegist();
        reqRegist.setEmail("test@example.com");
        reqRegist.setPassword("password123!");
        reqRegist.setName("홍길동");
        reqRegist.setAddress("서울시 강남구");
        reqRegist.setBirthDate(LocalDate.of(1990, 1, 1));
        reqRegist.setPhoneNumber("01012345678");
        reqRegist.setTermsAccept(true);

        ResponseEntity<ResNewAccessToken> response =
                authController.regist(reqRegist);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}