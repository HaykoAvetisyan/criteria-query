package com.example.criteriaquery.reposirory;

import com.example.criteriaquery.model.Employee;
import org.springframework.data.repository.CrudRepository;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {

}
