package com.example.awslambda.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class S3UploaderService {

    private final AmazonS3 s3Client;

    public void uploadFileToS3(File file, String bucketName, String keyName) {
        s3Client.putObject(new PutObjectRequest(bucketName, keyName, file));
    }
}

