package com.example.User.feign;

import com.example.User.dto.authserver.AuthServerEmailPinNumberRequest;
import com.example.User.dto.authserver.AuthServerPinNumberRequest;
import com.example.User.dto.businessnumber.BusinessNumberResponse;
import com.example.User.dto.corebank.AccountCheckRequest;
import com.example.User.dto.authserver.AuthServerProfileRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@FeignClient(name = "CoreBank", url = "http://3.39.182.226:3030")
public interface CoreBankFeign {

    @PostMapping("/businesscheck")
    BusinessNumberResponse checkBusinessNumber(@RequestBody String businessNumber);

    @PostMapping("/bank/verify-account")
    boolean verifyAccount(@RequestBody AccountCheckRequest accountCheckRequest);

    @PostMapping("/bank/verify-account-employee")
    boolean verifyAccountEmployee(@RequestBody AccountCheckRequest accountCheckRequest);

    @PostMapping("/authentication/profile/check")
    boolean verifyProfile(@RequestBody AuthServerProfileRequest profileRequest);

    @PostMapping("/authentication/email/pincheck")
    boolean checkEmailPinNumber(@RequestBody AuthServerEmailPinNumberRequest emailPinNumber);

    @PostMapping("/authentication/pincheck")
    boolean checkPinNumber(@RequestBody AuthServerPinNumberRequest checkPinNumber);
}
