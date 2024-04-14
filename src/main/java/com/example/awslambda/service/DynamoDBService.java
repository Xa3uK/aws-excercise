package com.example.awslambda.service;

import com.example.awslambda.model.DataExample;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
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

    public void insertData(DataExample dataExample) {
        Map<String, AttributeValue> itemValues = new HashMap<>();
        itemValues.put(KEY, AttributeValue.builder().s(dataExample.getId()).build());

        dataExample.getInputData()
            .forEach((key, value) -> itemValues.put(key, AttributeValue.builder().s(value).build()));

        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(itemValues)
            .build();

        dynamoDbClient.putItem(request);
    }

    public DataExample getDataById(String id) {
        HashMap<String, AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put(KEY, AttributeValue.builder()
            .s(id)
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
                DataExample dataExample = new DataExample();
                dataExample.setId(id);
                Map<String, String> attributes = returnedItem.entrySet().stream()
                    .filter(entry -> !entry.getKey().equals("id"))
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().s()));

                dataExample.setInputData(attributes);
                log.info("getDataById: {}", dataExample);
                return dataExample;
            }
        } catch (DynamoDbException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public List<DataExample> getAllData() {
        try {
            ScanRequest scanRequest = ScanRequest.builder()
                .tableName(tableName)
                .build();

            ScanResponse response = dynamoDbClient.scan(scanRequest);
            List<DataExample> dataExampleList = new ArrayList<>();
            for (Map<String, AttributeValue> item : response.items()) {
                DataExample dataExample = new DataExample();
                String id = item.get(KEY).s();
                dataExample.setId(id);

                Map<String, String> attributes = item.entrySet().stream()
                    .filter(entry -> !entry.getKey().equals(KEY))
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().s()));

                dataExample.setInputData(attributes);
                dataExampleList.add(dataExample);
            }
            dataExampleList.sort(Comparator.comparing(DataExample::getId));
            log.info("getAllData: {}", dataExampleList);
            return dataExampleList;

        } catch (DynamoDbException e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
