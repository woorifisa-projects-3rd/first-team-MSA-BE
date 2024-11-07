package com.example.User.service;

import com.example.User.dto.employee.EmployeeRequest;

public interface EmployeeService {

    void registerEmployee(EmployeeRequest employeeRequest);

    void modifyEmployee(EmployeeRequest employeeRequest);
}
