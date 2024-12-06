package com.example.Finance.service;

import com.example.Finance.dto.TransactionChartResponse;
import com.example.Finance.dto.TransactionHistoryRequest;
import com.example.Finance.dto.TransactionHistoryResponse;
import com.example.Finance.dto.TransactionHistoryWithCounterPartyResponse;
import com.example.Finance.error.CustomException;
import com.example.Finance.error.ErrorCode;
import com.example.Finance.feign.CoreBankFeign;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionHistoryService {
    private final CoreBankFeign coreBankFeign;

    //차트용 데이터 받아오기
    public TransactionChartResponse getTransactionChartData(
            TransactionHistoryRequest transactionHistoryRequest,
            Integer year,
            Integer month
    ) {
        //데이터 받기(년+월)
        List<TransactionHistoryResponse> transactionHistoryResponses = getYearMonthlyTransactions(
                transactionHistoryRequest, year, month
        );

        //데이터 받기(년)
        List<TransactionHistoryResponse> transactionHistoryResponsesYear = getYearlyTransactions(
                transactionHistoryRequest, year
        );

        //차트 데이터 생성
        TransactionChartResponse transactionChartResponse = new TransactionChartResponse();

        transactionChartResponse.setSales(
                calculateMonthlySales(transactionHistoryResponses)
        );
        transactionChartResponse.setExpenses(
                calculateMonthlyExpenses(transactionHistoryResponses)
        );

        Map<Integer, Long> monthlySalesMap = calculateMonthlySalesDetail(transactionHistoryResponsesYear);
        List<Long> monthlySalesData = new ArrayList<>(12);
        for (int i = 1; i <= 12; i++) {
            monthlySalesData.add(i - 1, monthlySalesMap.getOrDefault(i, 0L));
        }
        transactionChartResponse.setMonthlySales(monthlySalesData);

        transactionChartResponse.setTotalSales(
                calculateTotalSales(transactionHistoryResponses)
        );
        transactionChartResponse.setTotalExpenses(
                calculateTotalExpenses(transactionHistoryResponses)
        );

        return transactionChartResponse;
    }

    //요청받은 REQUEST에 따라, LIST로 거래 내역 받아오기
    public List<TransactionHistoryResponse> getYearMonthlyTransactions(
            TransactionHistoryRequest transactionHistoryRequest,
            Integer year,
            Integer month) {
        try {
            return coreBankFeign.getTransactionHistoryList(transactionHistoryRequest, year, month);
        } catch (FeignException e) {
            log.error("코어뱅킹 서비스 통신 중 오류 발생 - year: {}, month: {}", year, month, e);
            throw new CustomException(ErrorCode.BANKING_FEIGN_ERROR);
        } catch (Exception e) {
            log.error("통신 중 예상치 못한 오류 발생 - year: {}, month: {}", year, month, e);
            throw new CustomException(ErrorCode.SERVER_ERROR);
        }
    }

    public List<TransactionHistoryWithCounterPartyResponse> getYearMonthlyTransactionsWithCounterPartyName(
            TransactionHistoryRequest transactionHistoryRequest,
            Integer year,
            Integer month) {
        try {
            return coreBankFeign.getTransactionHistoryYearSalesListWithCounterParty(transactionHistoryRequest, year, month);
        } catch (FeignException e) {
            log.error("코어뱅킹 서비스 통신 중 오류 발생 - year: {}, month: {}", year, month, e);
            throw new CustomException(ErrorCode.BANKING_FEIGN_ERROR);
        } catch (Exception e) {
            log.error("통신 중 예상치 못한 오류 발생 - year: {}, month: {}", year, month, e);
            throw new CustomException(ErrorCode.SERVER_ERROR);
        }
    }

    public List<TransactionHistoryResponse> getYearlyTransactions(
            TransactionHistoryRequest transactionHistoryRequest,
            Integer year) {
        try {
            return coreBankFeign.getTransactionHistoryYearSalesList(transactionHistoryRequest, year);
        } catch (FeignException e) {
            log.error("코어뱅킹 서비스 통신 중 오류 발생 - year: {}", year, e);
            throw new CustomException(ErrorCode.BANKING_FEIGN_ERROR);
        } catch (Exception e) {
            log.error("통신 중 예상치 못한 오류 발생 - year: {}", year, e);
            throw new CustomException(ErrorCode.SERVER_ERROR);
        }
    }

    public Map<Integer, Long> calculateMonthlySalesDetail(
            List<TransactionHistoryResponse> transactionHistoryResponses
    ) {
        return transactionHistoryResponses.stream()
                .filter(TransactionHistoryResponse::getIsDeposit)
                .collect(Collectors.groupingBy(
                        tr -> Integer.valueOf(tr.getTransactionDate().split("-")[1]),
                        Collectors.summingLong(TransactionHistoryResponse::getAmount)
                ));
    }

    public List<TransactionHistoryResponse> calculateMonthlySales(
            List<TransactionHistoryResponse> transactionHistoryResponses
    ) {
        return transactionHistoryResponses.stream()
                .filter(TransactionHistoryResponse::getIsDeposit)
                .collect(Collectors.toList());
    }

    public List<TransactionHistoryResponse> calculateMonthlyExpenses(
            List<TransactionHistoryResponse> transactionHistoryResponses
    ) {
        return transactionHistoryResponses.stream()
                .filter(tr -> !tr.getIsDeposit())
                .collect(Collectors.toList());
    }

    public long calculateTotalSales(
            List<TransactionHistoryResponse> transactionHistoryResponses
    ) {
        return transactionHistoryResponses.stream()
                .filter(TransactionHistoryResponse::getIsDeposit)
                .mapToLong(TransactionHistoryResponse::getAmount)
                .sum();
    }

    public long calculateTotalExpenses(
            List<TransactionHistoryResponse> transactionHistoryResponses
    ) {
        return transactionHistoryResponses.stream()
                .filter(tr -> !tr.getIsDeposit())
                .mapToLong(TransactionHistoryResponse::getAmount)
                .sum();
    }




}
