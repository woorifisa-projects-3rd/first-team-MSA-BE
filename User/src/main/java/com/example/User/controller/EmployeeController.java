package com.example.User.controller;

import com.example.User.dto.employee.EmployeeRequest;
import com.example.User.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/employee/info")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("/regist")
    public ResponseEntity<Void> registerEmployee(@RequestBody EmployeeRequest employeeRequest) {
        employeeService.registerEmployee(employeeRequest);
        return ResponseEntity.ok().build();
    }
}
