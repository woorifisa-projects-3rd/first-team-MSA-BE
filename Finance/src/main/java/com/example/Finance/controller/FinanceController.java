package com.example.Finance.controller;

import com.example.Finance.dto.TransactionHistoryRequest;
import com.example.Finance.dto.TransactionHistoryResponse;
import com.example.Finance.feign.UserFeign;
import com.example.Finance.service.PdfService;
import com.example.Finance.service.IncomeStatementService;
import com.example.Finance.service.TransactionHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FinanceController {

    private final TransactionHistoryService transactionHistoryService;
    private final IncomeStatementService incomeStatementService;
    private final PdfService pdfService;
    private final UserFeign userFeign;

    //차트 제공에 맞춰서 변경하기
    @GetMapping("/transactionList")
    public List<TransactionHistoryResponse> getFinanaceData(
            @RequestParam Integer storeid,
            @RequestParam Integer year,
            @RequestParam Integer month)
    {
        TransactionHistoryRequest transactionHistoryRequest= TransactionHistoryRequest.from(userFeign.getStoreAccountInfo(storeid));
        return transactionHistoryService.getTransactionHistoryList(transactionHistoryRequest, year ,month);
    }

    //pdf 생성
    @CrossOrigin(origins = "*")
    @GetMapping(value = "/transactionpdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getFinancePdf(
            @RequestParam Integer storeid,
            @RequestParam Integer year,
            @RequestParam Integer month
    ) {
        TransactionHistoryRequest transactionHistoryRequest = TransactionHistoryRequest.from(userFeign.getStoreAccountInfo(storeid));
        List<TransactionHistoryResponse> transactionHistoryResponseList = transactionHistoryService.getTransactionHistoryList(transactionHistoryRequest, year, month);

        // PDF 생성
        byte[] pdfContent = pdfService.generateIncomeStatementPdf(
                transactionHistoryResponseList,
                incomeStatementService.calculateStatement(transactionHistoryResponseList)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        // 파일명 설정 (한글 지원)
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(String.format("%d년_%d월_손익계산서.pdf", year, month), StandardCharsets.UTF_8)
                .build());

        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }




}