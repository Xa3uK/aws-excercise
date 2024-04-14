package com.example.awslambda.uploader;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.File;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class S3Uploader {

    @Value("${aws.s3.access-key}")
    private String accessKey;
    @Value("${aws.s3.secret-key}")
    private String secretKey;

    @Value("${aws.s3.endpoint}")
    private String s3endpoint;

    @Value("${aws.s3.region}")
    private String s3region;

    public void uploadFileToS3(File file, String bucketName, String keyName) {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                s3endpoint,
                s3region))
            .build();

        s3Client.putObject(new PutObjectRequest(bucketName, keyName, file));
    }
}

