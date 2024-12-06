package com.example.Attendance.feign;

import com.example.Attendance.config.FeignConfig;
import com.example.Attendance.dto.batch.TransferRequest;
import com.example.Attendance.dto.batch.TransferResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@FeignClient(name = "CoreBank", url = "http://3.39.182.226:3030", configuration = FeignConfig.class)
public interface FeignWithCoreBank {

    @PostMapping("/bank/automatictransfer")
    TransferResponse automaticTransfer(@RequestBody TransferRequest transferRequest);
}
