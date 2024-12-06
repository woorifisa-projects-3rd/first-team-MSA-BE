package com.example.User.controller;

import com.example.User.dto.passwordemail.EmailOnlyRequest;
import com.example.User.dto.login.ReqIdFindData;
import com.example.User.dto.login.ReqPwChange;
import com.example.User.dto.login.ResIdFindData;
import com.example.User.dto.passwordemail.EmailRequest;
import com.example.User.dto.response.ResponseDto;
import com.example.User.model.President;
import com.example.User.resolver.MasterId;
import com.example.User.service.EmailService;
import com.example.User.service.PresidentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/president")
@Slf4j
@RequiredArgsConstructor
public class PresidentAccountController {

    private final PresidentService presidentService;
    private final EmailService emailService;

    @PostMapping("/check/email")
    public ResponseEntity<String> sendPinNumberToEmail(@RequestBody EmailOnlyRequest emailOnlyRequest) {
        log.info("email {}", emailOnlyRequest.getEmail());

        // 여기에 이메일 존재 여부 판별 코드를 작성
        presidentService.emialvalidation(emailOnlyRequest.getEmail());

        String pinNumber=emailService.sendPinNumberToEmail(emailOnlyRequest.getEmail());
        log.info("email send {}", pinNumber);
        return ResponseEntity.ok(pinNumber);
    }

    @PutMapping("/change-password")
    public ResponseEntity<ResponseDto> changePassword(@MasterId Integer id,
                                               @Valid @RequestBody ReqPwChange reqpwChange) {
        boolean result = presidentService.changePassword(id, reqpwChange);
        return result ?
                ResponseEntity.ok().build()
                : ResponseEntity.badRequest().body(ResponseDto.from("이전과 일치한 비밀번호입니다."));
    }

    @DeleteMapping("/secession")
    public ResponseEntity<Void> secession(@MasterId Integer id) {
        presidentService.remove(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> ResetPassword(@RequestBody @Valid EmailRequest emailRequest) {
        log.info("이메일 인증 이메일: " + emailRequest.getEmail());
        // 이메일과 이름 일치하는지 확인
        President president = presidentService.validateEmailAndName(emailRequest.getEmail(), emailRequest.getName());

        // 임시 비밀번호 생성하고 이메일 전송
        String temporaryPassword = emailService.temporaryPasswordEmail(emailRequest.getEmail());

        // 임시 비밀번호로 DB 업데이트
        presidentService.updatePassword(temporaryPassword, president);

        return ResponseEntity.ok("임시 비밀번호가 이메일로 전송되었습니다.");
    }

    @PostMapping("/id-find") //사장님 아이디 찾기
    public ResponseEntity<ResIdFindData> findId(@Valid @RequestBody ReqIdFindData reqIdFindData){
        String email = presidentService.findByNameAndPhoneNumber(reqIdFindData);
        return ResponseEntity.ok(ResIdFindData.from(email));
    }
}
