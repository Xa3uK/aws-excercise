package com.example.awslambda.exception;

public class EmployeeNotFoundException extends RuntimeException{

    public EmployeeNotFoundException(String message) {
        super("Employee with id: '%s' not found".formatted(message));
    }
}
