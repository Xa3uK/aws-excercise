package com.example.awslambda.service;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.example.awslambda.api.EmployeeApi;
import com.example.awslambda.exception.EmployeeNotFoundException;
import com.example.awslambda.model.Employee;
import com.example.awslambda.model.FileResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
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
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ReturnValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

@Service
@Slf4j
@RequiredArgsConstructor
public class AwsEmployeeService implements EmployeeApi {

    @Value("${aws.dynamodb.table-name}")
    private String tableName;
    private final S3Service s3Service;
    private final String KEY = "id";
    private final DynamoDbClient dynamoDbClient;
    private final AWSLambda lambdaClient;

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
        employee.setId(Long.parseLong(employeeId));
        log.info("Employee created: {}", employee);
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
                throw new EmployeeNotFoundException(id);
            } else {
                Employee employee = new Employee();
                employee.setId(Long.parseLong(id));
                Map<String, String> attributes = returnedItem.entrySet().stream()
                    .filter(entry -> !entry.getKey().equals(KEY))
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().s()));

                employee.setProfile(attributes);
                log.info("getEmployeeById: {}", employee);
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
                employee.setId(Long.parseLong(id));

                Map<String, String> attributes = item.entrySet().stream()
                    .filter(entry -> !entry.getKey().equals(KEY))
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().s()));

                employee.setProfile(attributes);
                employeeList.add(employee);
            }
            employeeList.sort(Comparator.comparing(Employee::getId));
            log.info("getAllEmployee: {}", employeeList);
            return employeeList;

        } catch (DynamoDbException e) {
            log.error(e.getMessage());
        }
        return null;
    }
    @SneakyThrows
    private String generateEmployeeId() {
        InvokeRequest invokeRequest = new InvokeRequest();
        invokeRequest.setFunctionName("IdGeneratorLambda");

        InvokeResult invokeResult = lambdaClient.invoke(invokeRequest);

        String payload = new String(invokeResult.getPayload().array(), StandardCharsets.UTF_8);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(payload);

        return jsonNode.get("body").get("next_id").asText();
    }

    @Override
    public void addAvatar(MultipartFile file, String id) {
        Employee employee = getEmployeeById(id);
        if (employee == null){
            throw new EmployeeNotFoundException(id);
        }
        s3Service.uploadFile(file);

        Map<String, AttributeValue> key = new HashMap<>();
        key.put(KEY, AttributeValue.builder().n(id).build());

        String updateExpression = "SET avatar = :value";

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":value", AttributeValue.builder().s(file.getOriginalFilename()).build());

        UpdateItemRequest request = UpdateItemRequest.builder()
            .tableName(tableName)
            .key(key)
            .updateExpression(updateExpression)
            .expressionAttributeValues(expressionAttributeValues)
            .returnValues(ReturnValue.ALL_NEW)
            .build();

        dynamoDbClient.updateItem(request);
        log.info("Avatar for employee '{}' added", id);
    }

    @Override
    public FileResponse getEmployeeAvatar(String id) throws IOException {
        Employee employee = getEmployeeById(id);
        if (employee == null){
            throw new EmployeeNotFoundException(id);
        }
        String avatarFileName = employee.getProfile().get("avatar");
        if (avatarFileName == null){
            throw new BadRequestException("Employee has no avatar yet");
        }
        return s3Service.getFileByName(avatarFileName);
    }
}
