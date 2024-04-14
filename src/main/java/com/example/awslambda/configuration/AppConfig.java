package com.example.awslambda.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AppConfig {

    @Value("${aws.s3.access-key}")
    private String accessKey;
    @Value("${aws.s3.secret-key}")
    private String secretKey;

    @Value("${aws.s3.endpoint}")
    private String s3endpoint;

    @Value("${aws.s3.region}")
    private String s3region;

    @Bean
    public DynamoDbClient getDynamoDbClient() {
        return DynamoDbClient.builder()
            .region(Region.EU_CENTRAL_1)
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)))
            .build();
    }

    @Bean
    public S3Client createS3Client() {
        return S3Client.builder()
            .region(Region.EU_CENTRAL_1)
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
            ))
            .build();
    }
}
