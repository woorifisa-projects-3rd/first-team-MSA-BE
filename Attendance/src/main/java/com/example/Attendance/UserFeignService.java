package com.example.Attendance;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Component
@FeignClient(name = "User")
public interface UserFeignService {

    @PostMapping("/employee/masking/list")
    Boolean sendMaskingIds(@RequestBody List<Integer> maskingIds);
}