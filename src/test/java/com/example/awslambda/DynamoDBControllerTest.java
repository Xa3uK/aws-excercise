package com.example.awslambda;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.awslambda.controller.DynamoDBController;
import com.example.awslambda.model.DataExample;
import com.example.awslambda.service.DynamoDBService;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class DynamoDBControllerTest {

    @Test
    public void testGetDataById_WithDataExampleExists() {
        DynamoDBService dynamoDBService = mock(DynamoDBService.class);
        when(dynamoDBService.getDataById("1")).thenReturn(new DataExample("3", Map.of("name", "Alberto")));
        DynamoDBController controller = new DynamoDBController(dynamoDBService);

        ResponseEntity<DataExample> responseEntity = controller.getDataById("1");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    public void testGetDataById_WithDataExampleNotFound() {
        DynamoDBService dynamoDBService = mock(DynamoDBService.class);
        when(dynamoDBService.getDataById("2")).thenReturn(null);
        DynamoDBController controller = new DynamoDBController(dynamoDBService);

        ResponseEntity<DataExample> responseEntity = controller.getDataById("2");

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }
}
