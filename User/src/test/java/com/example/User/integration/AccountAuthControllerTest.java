package com.example.User.integration;

import com.example.User.controller.AccountAuthController;
import com.example.User.dto.authserver.AuthServerEmailPinNumberRequest;
import com.example.User.dto.authserver.AuthServerOnlyPinNumberRequest;
import com.example.User.dto.authserver.AuthServerPinNumberRequest;
import com.example.User.dto.authserver.AuthServerProfileRequest;
import com.example.User.dto.corebank.AccountAndCodeRequest;
import com.example.User.dto.response.ResponseDto;
import com.example.User.error.CustomException;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

@SpringBootTest(properties = {
})
@Transactional
class AccountAuthControllerTest {
    @Autowired
    private AccountAuthController accountAuthController;

    @Test
    @DisplayName("사장 계좌 정보 확인")
    public void BusinessNumberCheck()
    {
        ResponseEntity<ResponseDto> accountcheck = accountAuthController.getAccountBankCodeAndAccountNumber(1,
                AccountAndCodeRequest.of("11111111111","020"));

        Assertions.assertEquals(accountcheck.getBody().getMessage(), "ok");
    }

    @Test
    @DisplayName("사장 계좌 정보 확인 실패")
    public void BusinessNumberCheckFail()
    {
        ResponseEntity<ResponseDto> accountcheck = accountAuthController.getAccountBankCodeAndAccountNumber(1,
                AccountAndCodeRequest.of("111111111112","020"));

        Assertions.assertEquals(accountcheck.getBody().getMessage(), "일치하지 않는 계좌 정보입니다");
    }


}