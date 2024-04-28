package com.example.awslambda.service;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.example.awslambda.model.Employee;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class DynamoDBService {

    @Value("${aws.dynamodb.table-name}")
    private String tableName;
    private final String KEY = "id";
    private final DynamoDbClient dynamoDbClient;
    private final AWSLambda lambda;

    public Employee createEmployee(Employee employee) {
        Map<String, AttributeValue> itemValues = new HashMap<>();
        String employeeId = generateEmployeeId();
        itemValues.put(KEY, AttributeValue.builder().n(employeeId).build());

        employee.getProfile()
            .forEach((key, value) -> itemValues.put(key, AttributeValue.builder().s(value).build()));

        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(itemValues)
            .build();

        dynamoDbClient.putItem(request);
        employee.setId(employeeId);
        return employee;
    }

    public Employee getEmployeeById(String id) {
        HashMap<String, AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put(KEY, AttributeValue.builder()
            .n(id)
            .build());

        GetItemRequest getItemRequest = GetItemRequest.builder()
            .tableName(tableName)
            .key(keyToGet)
            .build();

        try {
            Map<String, AttributeValue> returnedItem = dynamoDbClient.getItem(getItemRequest).item();
            if (returnedItem.isEmpty()) {
                log.error("No item found with the key {}", id);
            } else {
                Employee employee = new Employee();
                employee.setId(id);
                Map<String, String> attributes = returnedItem.entrySet().stream()
                    .filter(entry -> !entry.getKey().equals("id"))
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().s()));

                employee.setProfile(attributes);
                log.info("getDataById: {}", employee);
                return employee;
            }
        } catch (DynamoDbException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public List<Employee> getAllEmployee() {
        try {
            ScanRequest scanRequest = ScanRequest.builder()
                .tableName(tableName)
                .build();

            ScanResponse response = dynamoDbClient.scan(scanRequest);
            List<Employee> employeeList = new ArrayList<>();
            for (Map<String, AttributeValue> item : response.items()) {
                Employee employee = new Employee();
                String id = item.get(KEY).n();
                employee.setId(id);

                Map<String, String> attributes = item.entrySet().stream()
                    .filter(entry -> !entry.getKey().equals(KEY))
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().s()));

                employee.setProfile(attributes);
                employeeList.add(employee);
            }
            employeeList.sort(Comparator.comparing(Employee::getId));
            log.info("getAllData: {}", employeeList);
            return employeeList;

        } catch (DynamoDbException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @SneakyThrows
    private String generateEmployeeId() {
        InvokeRequest invokeRequest = new InvokeRequest();
        invokeRequest.setFunctionName("GetEmployeeIdFromDynamoDb");

        InvokeResult invokeResult = lambda.invoke(invokeRequest);

        String payload = new String(invokeResult.getPayload().array(), StandardCharsets.UTF_8);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(payload);

        return jsonNode.get("body").get("next_id").asText();
    }
}
