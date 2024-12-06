package com.example.Finance.integration;

import com.example.Finance.controller.FinanceController;
import com.example.Finance.dto.TransactionChartResponse;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
@Transactional
@Slf4j
public class FinanceIntegrationTests {

    @Autowired
    private FinanceController financeController;

    @Test
    @DisplayName("차트 데이터 (2024-11년 가져오기)")
    public void 차트데이터_성공()
    {
        //when
        ResponseEntity<TransactionChartResponse> testchart =  financeController.
                getFinanaceData(1,2024,11);

        //given

        Assertions.assertThat(HttpStatus.OK == testchart.getStatusCode());

        Assertions.assertThat(testchart.getBody().getExpenses().stream()
                .mapToInt(expense -> expense.getAmount())
                .sum()!= 0 );

    }

    @Test
    @DisplayName("차트 데이터 (존재하지 않는 데이터 가져오기 -> noexception)")
    public void 차트데이터_실패_WITHOUT_EXCEPTION()
    {
        //when
        ResponseEntity<TransactionChartResponse> testchart =  financeController.
                getFinanaceData(1,2029,11);

        //given

        Assertions.assertThat(HttpStatus.OK == testchart.getStatusCode());

        Assertions.assertThat(testchart.getBody().getExpenses().stream()
                .mapToInt(expense -> expense.getAmount())
                .sum()== 0 );

        Assertions.assertThat(testchart.getBody().getExpenses()).isEmpty();
        Assertions.assertThat(testchart.getBody().getSales()).isEmpty();

    }

    @Test
    @DisplayName("차트 데이터 (존재하지 않는 사장님 ID")
    public void 차트데이터_WITH_EXCEPTION()
    {
        int invalidOwnerId = 12323;

        Assertions.assertThatThrownBy(() ->
                        financeController.getFinanaceData(invalidOwnerId, 2029, 11))
                .isInstanceOf(FeignException.NotFound.class)
                .hasMessageContaining("가게가 존재하지 않습니다");


    }

}
