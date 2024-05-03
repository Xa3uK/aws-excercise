package com.example.awslambda.api;

import com.example.awslambda.model.Employee;
import com.example.awslambda.model.FileResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface EmployeeApi {

    Employee createEmployee(Employee employee);

    Employee getEmployeeById(String id);

    List<Employee> getAllEmployee();

    void addAvatar(MultipartFile file, String id);

    FileResponse getEmployeeAvatar(String id) throws IOException;
}
