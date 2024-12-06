package com.example.core_bank.core_bank.core.controller;

import com.example.core_bank.core_bank.core.dto.TransactionHistoryRequest;
import com.example.core_bank.core_bank.core.dto.TransactionHistoryResponse;
import com.example.core_bank.core_bank.core.dto.TransactionHistoryWithCounterPartyResponse;
import com.example.core_bank.core_bank.core.service.TransactionHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bank")
@Slf4j
@RequiredArgsConstructor
public class TransactionHistoryController {

    private final TransactionHistoryService transactionHistoryService;

    @PostMapping("/list")
    public ResponseEntity<List<TransactionHistoryResponse>> getTransactionHistory(
            @RequestBody TransactionHistoryRequest request,
            @RequestParam Integer year,
            @RequestParam Integer month)
    {

        String bankCode = request.getBankCode();
        String accountNumber = request.getAccount();

        log.info("bankcode, accountnumber" + bankCode + accountNumber);

        // 서비스 레이어를 통해 거래 내역을 조회
        List<TransactionHistoryResponse> transactionHistoryRes = transactionHistoryService
                .getTransactionHistory(bankCode, accountNumber, year, month);

        log.info(transactionHistoryRes.size()+ " ");

        return ResponseEntity.ok(transactionHistoryRes);
    }

    //년별 데이터중 , 매출만 조회
    @PostMapping("/year/list")
    public ResponseEntity<List<TransactionHistoryResponse>> getTransactionHistoryYearSalesList
            (@RequestBody TransactionHistoryRequest request,
             @RequestParam Integer year)
    {
        String bankCode = request.getBankCode();
        String accountNumber = request.getAccount();

        List<TransactionHistoryResponse> transactionHistoryRes = transactionHistoryService
                .getTransactionYearSalesHistory(bankCode, accountNumber, year);

        return ResponseEntity.ok(transactionHistoryRes);

    }

    //간편장부를 위한 데이터 전송(이름포함) + feign이므로 response entity에 보내지 않음
    @PostMapping("/list-counterparty")
    public List<TransactionHistoryWithCounterPartyResponse> getTransactionHistoryYearSalesListWithCounterParty
            (@RequestBody TransactionHistoryRequest request,
             @RequestParam Integer year,
             @RequestParam Integer month)
    {
        String bankCode = request.getBankCode();
        String accountNumber = request.getAccount();

        return transactionHistoryService
                .getTransactionHistoryWithCounterParty(bankCode, accountNumber, year,month);
    }


}
