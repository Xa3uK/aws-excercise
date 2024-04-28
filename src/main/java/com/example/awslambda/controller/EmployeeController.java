package com.example.awslambda.controller;

import com.example.awslambda.model.Employee;
import com.example.awslambda.service.DynamoDBService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/employee")
@Slf4j
public class EmployeeController {
    private final DynamoDBService dynamoDBService;

    @PostMapping
    public ResponseEntity<Employee> createData(@RequestBody Employee employee) {

        employee.getProfile().forEach((key, value) -> log.info("Key: {}, value: {}", key, value));
      return ResponseEntity.ok(dynamoDBService.createEmployee(employee));
    }

    @GetMapping("{id}")
    public ResponseEntity<Employee> getDataById(@PathVariable String id) {
        Employee employee = dynamoDBService.getEmployeeById(id);

        if (employee != null) {
            return ResponseEntity.ok(employee);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public List<Employee> getAllData(){
        return dynamoDBService.getAllEmployee();
    }

}
