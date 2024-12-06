package com.example.User.integration;

import com.example.User.controller.PresidentInfoController;
import com.example.User.dto.president.PresidentInfoResponse;
import com.example.User.dto.president.TermAcceptRequest;
import com.example.User.dto.presidentupdate.PresidentUpdateRequest;
import com.example.User.error.CustomException;
import com.example.User.error.ErrorCode;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(properties = {
})
@Transactional
class PresidentInfoControllerTest {

    @Autowired
    private PresidentInfoController presidentInfoController;

    @Test
    void 사장_정보_수정_성공() {
        // given
        Integer masterId = 1;
        PresidentUpdateRequest request = new PresidentUpdateRequest(
                "01012345678",
                LocalDate.now()
        );

        // when
        ResponseEntity<Void> response = presidentInfoController.updatePresident(masterId, request);

        // then
        Assertions.assertEquals(response.getStatusCode(),(HttpStatus.OK));
    }

    @Test
    void 사장_정보_조회_성공() {
        // given
        Integer masterId = 1;

        ResponseEntity<PresidentInfoResponse> response = presidentInfoController.getPresidentInfo(masterId);

        Assertions.assertEquals(response.getStatusCode(), (HttpStatus.OK));
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void 사장_정보_조회_실패() {
        // given
        Integer masterId = 128;

        CustomException exception = Assertions.assertThrows(CustomException.class,
                () -> presidentInfoController.getPresidentInfo(masterId));

        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void 약관_동의_성공() {
        // given
        Integer masterId = 1;
        TermAcceptRequest request = new TermAcceptRequest(true);

        // when
        ResponseEntity<Boolean> response = presidentInfoController.updateTermAccept(masterId, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isTrue();
    }
}