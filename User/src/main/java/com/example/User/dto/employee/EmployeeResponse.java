package com.example.User.dto.employee;

import lombok.AllArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
public class EmployeeResponse {
    private Integer id;
    private String name;
    private Boolean sex;
    private String address;
    private LocalDate birth_date;
    private Boolean employment_type;
    private String phone_number;
    private Integer payment_date;
    private Integer salary;
    private String account_number;
    private String bank_code;
    private String email;
    private Integer president_id;
}
