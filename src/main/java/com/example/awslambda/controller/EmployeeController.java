package com.example.awslambda.controller;

import com.example.awslambda.model.Employee;
import com.example.awslambda.model.FileResponse;
import com.example.awslambda.api.EmployeeApi;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeApi employeeApi;

    @PostMapping
    public ResponseEntity<Employee> createData(@RequestBody Employee employee) {
        return ResponseEntity.status(HttpStatusCode.CREATED).body(employeeApi.createEmployee(employee));
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable String id) {
        return ResponseEntity.ok(employeeApi.getEmployeeById(id));
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployee() {
        return ResponseEntity.ok(employeeApi.getAllEmployee());
    }

    @PostMapping("/{employeeId}/avatars")
    public ResponseEntity<?> addAvatar(@RequestParam("file") MultipartFile file, @PathVariable String employeeId) {
        employeeApi.addAvatar(file, employeeId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{employeeId}/avatars")
    public ResponseEntity<?> getAvatar(@PathVariable String employeeId) throws IOException {
        FileResponse fileResponse = employeeApi.getEmployeeAvatar(employeeId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(fileResponse.getMediaType());
        return new ResponseEntity<>(fileResponse.getContent(), httpHeaders, HttpStatus.OK);
    }
}
