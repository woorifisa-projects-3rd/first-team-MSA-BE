package com.example.Finance.feign;


import com.example.Finance.dto.AccountInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(name = "User", url = "http://user.msanew:7070")
public interface UserFeign {

    @GetMapping("/storeAccount")
    public AccountInfoResponse getStoreAccountInfo(@RequestParam Integer storeId);
}
