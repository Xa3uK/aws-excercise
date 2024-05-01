package com.example.awslambda;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.awslambda.controller.EmployeeController;
import com.example.awslambda.model.Employee;
import com.example.awslambda.service.EmployeeService;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class EmployeeControllerTest {

    @Test
    public void testGetEmployeeById_WithEmployeeExists() {
        EmployeeService employeeService = mock(EmployeeService.class);
        Employee employee = new Employee(3L, Map.of("David", "Perez"));
        when(employeeService.getEmployeeById("1")).thenReturn(employee);
        EmployeeController controller = new EmployeeController(employeeService);

        ResponseEntity<?> responseEntity = controller.getEmployeeById("1");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(employee, responseEntity.getBody());
    }
}
