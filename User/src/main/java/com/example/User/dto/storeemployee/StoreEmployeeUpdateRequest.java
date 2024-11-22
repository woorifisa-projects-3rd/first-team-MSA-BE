package com.example.User.dto.storeemployee;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StoreEmployeeUpdateRequest {

    private String email;
    private String name;
    private LocalDate birthDate;
    private Boolean sex;
    private String phoneNumber;
    private Byte employmentType;
    private String bankCode;
    private String accountNumber;
    private Integer salary;
    @Min(1)
    @Max(28)
    private Integer paymentDate;
    private String address;

    public static StoreEmployeeUpdateRequest of(
            String email, String name, LocalDate birthDate, Boolean sex, String phoneNumber, Byte employmentType,
            String bankCode, String accountNumber, Integer salary, Integer paymentDate, String address
    ) {
        return new StoreEmployeeUpdateRequest(
                email, name, birthDate, sex, phoneNumber,
                employmentType, bankCode, accountNumber, salary,
                paymentDate, address
        );
    }
}