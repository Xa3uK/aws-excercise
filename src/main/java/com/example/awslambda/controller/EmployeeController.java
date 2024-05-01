package com.example.awslambda.controller;

import com.example.awslambda.exception.EmployeeNotFoundException;
import com.example.awslambda.model.Employee;
import com.example.awslambda.model.FileResponse;
import com.example.awslambda.service.EmployeeService;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.http.HttpStatusCode;

@RequiredArgsConstructor
@RestController
@RequestMapping("/employee")
@Slf4j
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<Employee> createData(@RequestBody Employee employee) {
        return ResponseEntity.status(HttpStatusCode.CREATED).body(employeeService.createEmployee(employee));
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable String id) {
        try {
            Employee employee = employeeService.getEmployeeById(id);
            return ResponseEntity.ok(employee);
        } catch (EmployeeNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployee() {
        return ResponseEntity.ok(employeeService.getAllEmployee());
    }

    @PostMapping("/{id}/avatar")
    public ResponseEntity<?> addAvatar(@RequestParam("file") MultipartFile file, @PathVariable String id) {
        try {
            employeeService.addAvatar(file, id);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (EmployeeNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/avatar")
    public ResponseEntity<?> getAvatar(@PathVariable String id) {
        try {
            FileResponse fileResponse = employeeService.getEmployeeAvatar(id);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(fileResponse.getMediaType());
            return new ResponseEntity<>(fileResponse.getContent(), httpHeaders, HttpStatus.OK);
        } catch (FileNotFoundException | EmployeeNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
