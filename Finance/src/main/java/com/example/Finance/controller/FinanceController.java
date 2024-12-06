package com.example.Finance.controller;

import com.example.Finance.dto.TransactionChartResponse;
import com.example.Finance.dto.TransactionHistoryRequest;
import com.example.Finance.dto.TransactionHistoryResponse;
import com.example.Finance.dto.TransactionHistoryWithCounterPartyResponse;
import com.example.Finance.feign.UserFeign;
import com.example.Finance.service.SimpleLedgerPdfService;
import com.example.Finance.service.IncomeStatementPdfService;
import com.example.Finance.service.TransactionHistoryService;
import com.example.Finance.service.UserInteractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FinanceController {

    private final TransactionHistoryService transactionHistoryService;
    private final SimpleLedgerPdfService simpleLedgerPdfService;
    private final IncomeStatementPdfService incomeStatementPdfService;
    private final UserInteractService userInteractService;

    //차트 제공에 맞춰서 변경하기
    @GetMapping("/transactionchart")
    public ResponseEntity<TransactionChartResponse> getFinanaceData(
            @RequestParam Integer storeid,
            @RequestParam Integer year,
            @RequestParam Integer month)
    {
        TransactionHistoryRequest transactionHistoryRequest=
                TransactionHistoryRequest.from(userInteractService.getStoreAccountInfo(storeid));
        return ResponseEntity.ok(
                transactionHistoryService.getTransactionChartData(transactionHistoryRequest, year ,month));
    }

    //pdf 생성
    @PostMapping(value = "/transactionpdf")
    public ResponseEntity<byte[]> getFinancePdf(
            @RequestParam Integer storeid,
            @RequestParam Integer year,
            @RequestParam Integer month
    ) {
        TransactionHistoryRequest transactionHistoryRequest =
                TransactionHistoryRequest.from(userInteractService.getStoreAccountInfo(storeid));
        List<TransactionHistoryResponse> transactionHistoryResponseList = transactionHistoryService.getYearMonthlyTransactions(transactionHistoryRequest, year, month);

        // PDF 생성
        byte[] pdfContent = incomeStatementPdfService.generateIncomeStatementPdf(
                transactionHistoryResponseList
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }

    @PostMapping(value = "/simple-ledger-pdf")
    public ResponseEntity<byte[]> createSimpleLedgerPdf(
            @RequestParam Integer storeid,
            @RequestParam Integer year,
            @RequestParam Integer month,
            @RequestParam boolean taxtype
    ) {
        TransactionHistoryRequest transactionHistoryRequest =
                TransactionHistoryRequest.from(userInteractService.getStoreAccountInfo(storeid));

        //이매소드 말고, 송,수취인 정보 받는 메소드로 변경
        List<TransactionHistoryWithCounterPartyResponse> transactionHistoryResponseList = transactionHistoryService.getYearMonthlyTransactionsWithCounterPartyName(transactionHistoryRequest, year, month);

        // PDF 생성(매소드 바꿔서, 송,수취인 모든 기록 되는 html로)
        byte[] pdfContent = simpleLedgerPdfService.generatecounterPartyPdf(
                transactionHistoryResponseList, taxtype
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }




}
