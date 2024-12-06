package com.example.User.controller;


import com.example.User.dto.president.PresidentInfoResponse;
import com.example.User.dto.president.TermAcceptRequest;
import com.example.User.dto.presidentupdate.PresidentUpdateRequest;
import com.example.User.resolver.MasterId;
import com.example.User.service.PresidentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/president")
@Slf4j
@RequiredArgsConstructor
public class PresidentInfoController {
    private final PresidentService presidentService;

    //사장 정보수정 폰번호,생년월일
    @PutMapping("/modify")
    public ResponseEntity<Void> updatePresident(@MasterId Integer id,@RequestBody PresidentUpdateRequest
                                                 presidentUpdateRequest) {
        presidentService.updatePresident(id, presidentUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/mypage")
    public ResponseEntity<PresidentInfoResponse> getPresidentInfo(@MasterId Integer id)
    {
        return ResponseEntity.ok(presidentService.getPresidentInfo(id));
    }

    @PostMapping("/termaccept")
    public ResponseEntity<Boolean> updateTermAccept(@MasterId Integer id,@RequestBody TermAcceptRequest termAcceptRequest){
        Boolean result = presidentService.updateTermAccept(id,termAcceptRequest.getTermsAccept());

        return ResponseEntity.ok(result);
    }
}


