package com.iexceed.seatmanagement.employees.repository;

import com.iexceed.seatmanagement.employees.entity.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface EmployeeRepository extends MongoRepository<Employee, String> {

    Optional<Employee> findByEmployeeCode(String employeeCode);

    Optional<Employee> findByZohoEmployeeId(String zohoEmployeeId);

    Optional<Employee> findByEmail(String email);

    boolean existsByEmployeeCode(String employeeCode);
}