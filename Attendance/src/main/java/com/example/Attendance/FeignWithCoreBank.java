package com.example.Attendance;

import com.example.Attendance.config.FeignConfig;
import com.example.Attendance.dto.batch.TransferRequest;
import com.example.Attendance.dto.batch.TransferResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@FeignClient(name = "core",url= "http://localhost:3030/bank")
public interface FeignWithCoreBank {

    @PostMapping("/automaticTransfer")
    TransferResponse automaticTransfer(@RequestBody TransferRequest transferRequest);
}
