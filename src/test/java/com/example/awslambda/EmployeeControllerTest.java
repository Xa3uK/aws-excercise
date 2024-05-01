package com.example.awslambda;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

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
        when(employeeService.getEmployeeById("1")).thenReturn(new Employee(3L, Map.of("name", "Alberto")));
        EmployeeController controller = new EmployeeController(employeeService);

        ResponseEntity<Employee> responseEntity = controller.getEmployeeById("1");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    public void testGetEmployeeById_WithEmployeeNotFound() {
        EmployeeService employeeService = mock(EmployeeService.class);
        when(employeeService.getEmployeeById("2")).thenReturn(null);
        EmployeeController controller = new EmployeeController(employeeService);

        ResponseEntity<Employee> responseEntity = controller.getEmployeeById("2");

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }
}
