package com.example.User.service;

import com.example.User.dto.storeemployee.EmployeeInfoResponse;
import com.example.User.dto.storeemployee.StoreEmployeeRequest;
import com.example.User.dto.storeemployee.StoreEmployeeUpdateRequest;
import com.example.User.error.CustomException;
import com.example.User.error.ErrorCode;
import com.example.User.model.Store;
import com.example.User.model.StoreEmployee;
import com.example.User.repository.StoreEmployeeRepository;
import com.example.User.repository.StoreRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreEmployeeService {
    private final StoreRepository storeRepository;
    private final StoreEmployeeRepository storeEmployeeRepository;

    @Transactional
    public void register(@Valid StoreEmployeeRequest request, Integer storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_STORE));
        StoreEmployee storeEmployee = request.toEntity(store);
        storeEmployeeRepository.save(storeEmployee);
    }

    @Transactional
    public void updateEmployee(int storeEmployeeId, @Valid StoreEmployeeUpdateRequest request)
    {
        int rowsUpdated = storeEmployeeRepository.updateStoreEmployee(
                storeEmployeeId, request.getName(), request.getSex(),
                request.getAddress(), request.getBirthDate(), request.getPhoneNumber(),
                request.getEmail(), request.getSalary(), request.getEmploymentType(),
                request.getBankCode(), request.getAccountNumber(), request.getPaymentDate()
        );

        if (rowsUpdated == 0) {
            throw new CustomException(ErrorCode.STOREEMPLOYEE_NOT_FOUND);
        };
    }

    @Transactional
    public void deleteEmployee(int storeEmployeeId) {
        StoreEmployee employee = storeEmployeeRepository.findById(storeEmployeeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STOREEMPLOYEE_NOT_FOUND));
        storeEmployeeRepository.updateEmployeeReplaceDelete(employee.getId());
    }

    @Transactional
    public List<EmployeeInfoResponse> getEmployeeInfoByStore(Integer storeId)
    {
        return storeEmployeeRepository.findByStoreIdWithFetch(storeId)
                .stream().map(EmployeeInfoResponse::from).toList();
    }

    public String getEmail(Integer seId){
        StoreEmployee storeEmployee= storeEmployeeRepository.findById(seId)
                .orElseThrow(() -> new CustomException(ErrorCode.STOREEMPLOYEE_NOT_FOUND));
        return storeEmployee.getEmail();
    }

    @Transactional
    public Boolean updateMasking(List<Integer> ids){
        try {
            Byte newType = 11;
            int updatedCount = storeEmployeeRepository.updateEmploymentTypeByIds(ids, newType);
            return updatedCount > 0;  // 업데이트된 레코드가 있으면 true
        } catch (Exception e) {
            log.error("마스킹 업데이트 실패: {}", e.getMessage());
            return false;
        }
    }
}
