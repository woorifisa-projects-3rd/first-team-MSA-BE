package com.example.Attendance.feign;

import com.example.Attendance.config.FeignConfig;
import com.example.Attendance.dto.batch.TransferRequest;
import com.example.Attendance.dto.batch.TransferResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@FeignClient(name = "core",url= "http://localhost:3030/bank", configuration = FeignConfig.class)
public interface FeignWithCoreBank {

    @PostMapping("/automatictransfer")
    TransferResponse automaticTransfer(@RequestBody TransferRequest transferRequest);
}
