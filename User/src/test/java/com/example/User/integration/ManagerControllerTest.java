package com.example.User.integration;

import com.example.User.controller.ManagerController;
import com.example.User.dto.manager.ManagerResponse;
import com.example.User.error.CustomException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = {
})
@Transactional
class ManagerControllerTest {

    @Autowired
    private ManagerController managerController;

    @Test
    @DisplayName("[관리자] : 사장님 목록 조회 성공")
    public void getAllPresidents_success() {
        ResponseEntity<List<ManagerResponse>> response = managerController.getAllPresidents();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    @DisplayName("[관리자] : 사장님 삭제 성공")
    public void deletePresident_success() {
        Integer presidentId = 1;
        ResponseEntity<Void> response = managerController.deletePresident(presidentId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("관리자 확인 성공")
    public void checkManager_success() {
        Integer presidentId = 6;
        ResponseEntity<String> response = managerController.checkManager(presidentId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("관리자 확인");
    }


    @Test
    @DisplayName("관리자 확인 실패 - 권한 없음")
    public void checkManager_fail_unauthorized() {
        Integer invalidId = 9999;
        assertThrows(CustomException.class, () ->
                managerController.checkManager(invalidId)
        );
    }

    @Test
    @DisplayName("사장님 삭제 실패 - 존재하지 않는 ID")
    public void deletePresident_fail_notFound() {
        Integer invalidId = 9999;
        assertThrows(CustomException.class, () ->
                managerController.deletePresident(invalidId)
        );
    }


}