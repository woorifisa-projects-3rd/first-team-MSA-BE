package com.example.User.dto.employee;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EmployeeRequest {

    @NotNull
    private Integer id;

    @NotBlank
    private String name;

    @NotNull
    private Boolean sex;

    @NotBlank
    private String address;

    @NotNull
    private LocalDate birth_date;

    @NotNull
    private Boolean employment_type;

    @NotBlank
    private String phone_number;

    @NotNull
    @Min(1)
    @Max(31)
    private Integer payment_date;

    @NotNull
    private Integer salary;

    @NotBlank
    private String account_number;

    @NotBlank
    private String bank_code;

    @NotBlank
    @Email
    private String email;

    @NotNull
    private Integer president_id;

}
