package com.example.User.service;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@Component
@FeignClient(name = "User")
public interface FeignWithUser {

    @GetMapping("/refresh")
    public String requesttoCEO();
}
