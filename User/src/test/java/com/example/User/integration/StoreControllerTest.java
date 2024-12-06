package com.example.User.integration;

import com.example.User.controller.StoreController;
import com.example.User.dto.store.StoreRequest;
import com.example.User.dto.store.StoreResponse;
import com.example.User.dto.store.StoreUpdateRequest;
import com.example.User.error.CustomException;
import com.example.User.error.ErrorCode;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@SpringBootTest(properties = {
})
@Transactional
class StoreControllerTest {

    @Autowired
    private StoreController storeController;

    @Test
    void 매장_등록_성공() {
        Integer presidentId = 1;
        StoreRequest request = new StoreRequest(
                1,
                "테스트 매장",
                "123-45-67890",
                "1234567890",
                "004",
                "서울시 강남구 테헤란로",
                37.5012,
                127.0396
        );
        ResponseEntity<Void> response = storeController.store(presidentId, request);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void 매장_목록_조회_성공() {

        Integer presidentId = 1;
        ResponseEntity<List<StoreResponse>> response = storeController.showStores(presidentId);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    void 매장_정보_수정_성공() {
        Integer storeId = 1;
        StoreUpdateRequest request = new StoreUpdateRequest(
                "테스트 매장2",
                "123-45-67890",
                "서울시 강남구 테헤란로",
                37.5012,
                127.0396
        );
        ResponseEntity<Void> response = storeController.updateStoreAccount(storeId, request);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void 매장이름_중복확인_성공() {
        Integer storeId = 1;
        String storeName = "테스트 매장";
        ResponseEntity<Boolean> response = storeController.duplicateCheckStoreName(storeId, storeName);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    void 매장_삭제_성공() {
        Integer storeId = 1;
        ResponseEntity<Void> response = storeController.deleteStore(storeId);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void 존재하지_않는_매장_조회_건수_0() {
        Integer presidentId = 999;
        Assertions.assertEquals(storeController.showStores(presidentId).getBody().size(), 0);
    }

    @Test
    void 매장_수정_실패_매장없음() {
        // given
        Integer storeId = 999;
        StoreUpdateRequest request = new StoreUpdateRequest(
                "테스트 매장2",
                "123-45-67890",
                "서울시 강남구 테헤란로",
                37.5012,
                127.0396
        );

        CustomException exception = Assertions.assertThrows(CustomException.class,
                () -> storeController.updateStoreAccount(storeId, request));
        Assertions.assertEquals(ErrorCode.STORE_NOT_FOUND, exception.getErrorCode());
    }

}