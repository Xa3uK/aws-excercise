package com.example.awslambda.service;

import com.example.awslambda.model.InputData;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

@Service
public class DynamoDBService {

    @Value("${aws.s3.access-key}")
    private String accessKey;
    @Value("${aws.s3.secret-key}")
    private String secretKey;

    public void insertData(InputData inputData) {

        DynamoDbClient dynamoDb = getDynamoDbClient();
        Map<String, AttributeValue> itemValues = new HashMap<>();
        itemValues.put("id", AttributeValue.builder().s(inputData.getId()).build());

        inputData.getInputData()
            .forEach((key, value) -> itemValues.put(key, AttributeValue.builder().s(value).build()));

        PutItemRequest request = PutItemRequest.builder()
            .tableName("somedata")
            .item(itemValues)
            .build();

        dynamoDb.putItem(request);
    }

    private DynamoDbClient getDynamoDbClient() {
        return DynamoDbClient.builder()
            .region(Region.EU_CENTRAL_1)
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)))
            .build();
    }
}

