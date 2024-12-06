package com.example.User.integration;

import com.example.User.controller.StoreEmployeeController;
import com.example.User.dto.storeemployee.EmployeeInfoResponse;
import com.example.User.dto.storeemployee.StoreEmployeeRequest;
import com.example.User.dto.storeemployee.StoreEmployeeUpdateRequest;
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
import java.util.Arrays;
import java.util.List;

@SpringBootTest(properties = {
})
@Transactional
class StoreEmployeeControllerTest {

    @Autowired
    private StoreEmployeeController storeEmployeeController;

    @Test
    void 직원_등록_성공() {
        Integer storeId = 1;
        StoreEmployeeRequest request = StoreEmployeeRequest.of(
                "haha@example.com",
                "테스트직원",
                LocalDate.of(1990, 5, 15),
                true,
                "010-5471-5678",
                (byte)1,
                "101",
                "1234567890",
                3000000,
                15,
                "123 Main St"
        );
        ResponseEntity<String> response = storeEmployeeController.registerEmployee(request, storeId);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    void 직원_수정_성공() {
        Integer seId = 1;
        StoreEmployeeUpdateRequest request = StoreEmployeeUpdateRequest.of(
                "2@example.com",
                "테스트직원2",
                LocalDate.of(1990, 5, 15),
                true,
                "010-5471-5678",
                (byte)1,
                "101",
                "1234567890",
                3000000,
                15,
                "123 Main St"
        );
        ResponseEntity<String> response = storeEmployeeController.updateEmployee(seId, request);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("직원 수정 성공", response.getBody());
    }

    @Test
    void 직원_삭제_성공() {
        Integer seId = 1;
        ResponseEntity<String> response = storeEmployeeController.deleteEmployee(seId);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("직원 삭제 성공", response.getBody());
    }

    @Test
    void 매장별_직원목록_조회_성공() {
        Integer storeId = 1;
        ResponseEntity<List<EmployeeInfoResponse>> response = storeEmployeeController.getEmployeeInfoByStore(storeId);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    void URL_재전송_성공() {
        Integer seId = 1;
        Integer storeId = 1;
        ResponseEntity<String> response = storeEmployeeController.resendUrl(seId, storeId);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());  // URL이 반환되어야 함
    }

    @Test
    void 마스킹처리_성공() {
        List<Integer> ids = Arrays.asList(1, 2, 3);
        Boolean result = storeEmployeeController.getMaskingIds(ids);
        Assertions.assertTrue(result);
    }

    @Test
    void 존재하지_않는_직원_수정_실패() {
        Integer seId = 999;
        StoreEmployeeUpdateRequest request = StoreEmployeeUpdateRequest.of(
                "2@example.com",
                "테스트직원2",
                LocalDate.of(1990, 5, 15),
                true,
                "010-5471-5678",
                (byte)1,
                "101",
                "1234567890",
                3000000,
                15,
                "123 Main St"
        );
        CustomException exception = Assertions.assertThrows(CustomException.class,
                () -> storeEmployeeController.updateEmployee(seId, request));
        Assertions.assertEquals(ErrorCode.STOREEMPLOYEE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void 존재하지_않는_직원_삭제_실패() {
        Integer seId = 999;
        CustomException exception = Assertions.assertThrows(CustomException.class,
                () -> storeEmployeeController.deleteEmployee(seId));
        Assertions.assertEquals(ErrorCode.STOREEMPLOYEE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void 존재하지_않는_매장_직원목록_조회_실패() {
        Integer storeId = 999;
        Assertions.assertEquals(
                storeEmployeeController.getEmployeeInfoByStore(storeId).getBody().size(), 0);
    }

    @Test
    void 이메일_형식_오류로_직원등록_실패() {
        Integer storeId = 1;
        StoreEmployeeRequest request = StoreEmployeeRequest.of(
                "2example.com",
                "테스트직원2",
                LocalDate.of(1990, 5, 15),
                true,
                "010-5471-5678",
                (byte)1,
                "101",
                "1234567890",
                3000000,
                15,
                "123 Main St"
        );
        Exception exception = Assertions.assertThrows(Exception.class,
                () -> storeEmployeeController.registerEmployee(request, storeId));
    }

    @Test
    void URL_재전송_실패_존재하지_않는_직원() {
        Integer seId = 999;
        Integer storeId = 1;
        CustomException exception = Assertions.assertThrows(CustomException.class,
                () -> storeEmployeeController.resendUrl(seId, storeId));
        Assertions.assertEquals(ErrorCode.STOREEMPLOYEE_NOT_FOUND, exception.getErrorCode());
    }
}