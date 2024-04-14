package com.example.awslambda.service;

import java.io.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.HttpStatusCode;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;

    public void uploadFile(File file, String bucketName, String keyName) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName).key(keyName).build();

        s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));
    }

    public ResponseEntity<String> getFileFromS3(String filename, String bucketName) {

        GetObjectRequest objectRequest = GetObjectRequest
            .builder()
            .key(filename)
            .bucket(bucketName)
            .build();

        try {
            ResponseBytes<GetObjectResponse> objectAsBytes = s3Client.getObjectAsBytes(objectRequest);
            return ResponseEntity.ok(new String(objectAsBytes.asByteArray()));
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                log.error("File with name {} not found", filename);
                return  ResponseEntity.status(HttpStatusCode.NOT_FOUND).body("File not found");
            } else {
                log.error("Error retrieving file from S3: {}", e.getMessage());
                return ResponseEntity.status(HttpStatusCode.BAD_REQUEST).body("Error retrieving file from S3");
            }
        }
    }
}

