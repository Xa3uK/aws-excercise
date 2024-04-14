package com.example.awslambda.service;

import com.example.awslambda.model.DataExample;
import com.example.awslambda.processor.DataProcessor;
import java.io.File;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FileService {

    private final DataProcessor dataProcessor;
    private final S3Service s3Service;
    @Value("${aws.s3.bucket}")
    private String bucket;

    public void processData(DataExample dataExample) {
        File file = dataProcessor.processRequest(dataExample);
        s3Service.uploadFile(file, bucket, file.getName());
    }

    public ResponseEntity<String> getFileByName(String filename) {
        return s3Service.getFileFromS3(filename, bucket);
    }
}
