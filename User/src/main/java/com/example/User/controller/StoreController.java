package com.example.User.controller;

import com.example.User.dto.store.StoreUpdateRequest;
import com.example.User.dto.store.StoreRequest;
import com.example.User.dto.store.StoreResponse;
import com.example.User.resolver.MasterId;
import com.example.User.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    public ResponseEntity<Void> store(@MasterId Integer id, @RequestBody StoreRequest storeRequest) {
        storeService.registerStore(id,storeRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/storelist")
    public ResponseEntity<List<StoreResponse>> showStores(@MasterId Integer presidentId) {
        List<StoreResponse> storeResponse = storeService.showStores(presidentId);
        return ResponseEntity.ok(storeResponse);
    }


    @PutMapping
    public ResponseEntity<Void> updateStoreAccount(@RequestParam("storeid") Integer storeId,
                                                    @RequestBody StoreUpdateRequest request) {

        storeService.updateStore(storeId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/duplicate/name")
    public ResponseEntity<Boolean> duplicateCheckStoreName(@RequestParam("storeid") Integer storeId,
                                                            @RequestParam("storename") String storeName) {
        boolean result = storeService.duplicate(storeId, storeName);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteStore(@RequestParam("storeid") Integer storeId) {
        storeService.deleteStore(storeId);
        //id 확인 후 삭제
        return ResponseEntity.ok().build();
    }
}
