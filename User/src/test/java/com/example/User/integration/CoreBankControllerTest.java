package com.example.User.integration;

import com.example.User.controller.CoreBankController;
import com.example.User.dto.corebank.AccountAndCodeRequest;
import com.example.User.dto.corebank.AccountCheckRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

@SpringBootTest(properties = {
})
@Transactional
class CoreBankControllerTest {

    @Autowired
    public CoreBankController coreBankController;

    @Test
    @DisplayName("직원 계좌 확인")
    public void 게좌_확인_성공()
    {
            Assertions.assertEquals(coreBankController.getAccountBankcodeAndAccountNumberEmployeename(
                    new AccountCheckRequest("박준현", "020","11111111111")
            ).getStatusCode(), HttpStatus.OK);
    }

    @Test
    @DisplayName("직원 계좌 확인 실패")
    public void 게좌_확인_실패()
    {
        Assertions.assertEquals(coreBankController.getAccountBankcodeAndAccountNumberEmployeename(
                new AccountCheckRequest("악의적사용자", "020","11111111111")
        ).getBody(), false);
    }
}