package com.example.awslambda.service;

import com.example.awslambda.model.DataExample;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

@Service
@Slf4j
public class DynamoDBService {

    @Value("${aws.s3.access-key}")
    private String accessKey;
    @Value("${aws.s3.secret-key}")
    private String secretKey;

    @Value("${aws.dynamodb.table-name}")
    private String tableName;
    private final String KEY = "id";

    public void insertData(DataExample dataExample) {

        DynamoDbClient dynamoDb = getDynamoDbClient();
        Map<String, AttributeValue> itemValues = new HashMap<>();
        itemValues.put(KEY, AttributeValue.builder().s(dataExample.getId()).build());

        dataExample.getInputData()
            .forEach((key, value) -> itemValues.put(key, AttributeValue.builder().s(value).build()));

        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(itemValues)
            .build();

        dynamoDb.putItem(request);
    }

    public DataExample getDataById(String id) {
        DynamoDbClient dynamoDb = getDynamoDbClient();
        HashMap<String, AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put(KEY, AttributeValue.builder()
            .s(id)
            .build());

        GetItemRequest getItemRequest = GetItemRequest.builder()
            .tableName(tableName)
            .key(keyToGet)
            .build();

        try {
            Map<String, AttributeValue> returnedItem = dynamoDb.getItem(getItemRequest).item();
            if (returnedItem.isEmpty()) {
                log.error("No item found with the key {}", id);
            } else {
                DataExample dataExample = new DataExample();
                dataExample.setId(id);
                Map<String, String> attributes = returnedItem.entrySet().stream()
                    .filter(entry -> !entry.getKey().equals("id"))
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().s()));

                dataExample.setInputData(attributes);
                return dataExample;
            }
        } catch (DynamoDbException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    private DynamoDbClient getDynamoDbClient() {
        return DynamoDbClient.builder()
            .region(Region.EU_CENTRAL_1)
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)))
            .build();
    }
}
