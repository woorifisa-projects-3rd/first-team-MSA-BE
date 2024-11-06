package com.example.User.service;

import com.example.User.dto.employee.EmployeeRequest;
import com.example.User.model.Employee;
import com.example.User.model.President;
import com.example.User.respository.EmployeeRepository;
import com.example.User.respository.PresidentRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService{

    private final EmployeeRepository employeeRepository;
    private final PresidentRepository presidentRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, PresidentRepository presidentRepository) {
        this.employeeRepository = employeeRepository;
        this.presidentRepository = presidentRepository;
    }

    @Override
    public void registerEmployee(EmployeeRequest employeeRequest) {
        President president = presidentRepository.findById(1).orElseThrow();
        Employee employee = Employee.createEmployee(
                employeeRequest.getName(),
                employeeRequest.getSex(),
                employeeRequest.getAddress(),
                employeeRequest.getBirth_date(),
                employeeRequest.getEmployment_type(),
                employeeRequest.getPhone_number(),
                employeeRequest.getPayment_date(),
                employeeRequest.getSalary(),
                employeeRequest.getAccount_number(),
                employeeRequest.getBank_code(),
                employeeRequest.getEmail(),
                president
        );
        employeeRepository.save(employee);
    }
}
