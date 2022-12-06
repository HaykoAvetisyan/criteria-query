package com.example.criteriaquery.service;


import com.example.criteriaquery.model.Employee;
import com.example.criteriaquery.model.EmployeePage;
import com.example.criteriaquery.model.EmployeeSearchCriteria;
import com.example.criteriaquery.reposirory.EmployeeCriteriaRepository;
import com.example.criteriaquery.reposirory.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeCriteriaRepository employeeCriteriaRepository;

    public EmployeeService(EmployeeRepository employeeRepository, EmployeeCriteriaRepository employeeCriteriaRepository) {
        this.employeeRepository = employeeRepository;
        this.employeeCriteriaRepository = employeeCriteriaRepository;
    }

    public Page<Employee> getEmployees(EmployeePage employeePage, EmployeeSearchCriteria employeeSearchCriteria){
        return employeeCriteriaRepository.findAllFilters(employeePage, employeeSearchCriteria);
    }

    public Employee addEmployee(Employee employee){
        return employeeRepository.save(employee);
    }
}
