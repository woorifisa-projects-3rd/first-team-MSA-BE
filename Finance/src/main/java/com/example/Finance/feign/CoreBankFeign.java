package com.example.Finance.feign;

import com.example.Finance.dto.TransactionHistoryRequest;
import com.example.Finance.dto.TransactionHistoryResponse;
import com.example.Finance.dto.TransactionHistoryWithCounterPartyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Component
@FeignClient(name = "CoreBank", url = "http://3.39.182.226:3030")
public interface CoreBankFeign {

    @PostMapping("/bank/list")
    List<TransactionHistoryResponse> getTransactionHistoryList
            (@RequestBody TransactionHistoryRequest transactionHistoryRequest,
             @RequestParam Integer year,
             @RequestParam Integer month);

    //년 별 매출 only 조회 feign
    @PostMapping("/bank/year/list")
    List<TransactionHistoryResponse> getTransactionHistoryYearSalesList
            (@RequestBody TransactionHistoryRequest transactionHistoryRequest,
             @RequestParam Integer year);

    @PostMapping("/bank/list-counterparty")
    public List<TransactionHistoryWithCounterPartyResponse> getTransactionHistoryYearSalesListWithCounterParty
            (@RequestBody TransactionHistoryRequest request,
             @RequestParam Integer year,
             @RequestParam Integer month);
}