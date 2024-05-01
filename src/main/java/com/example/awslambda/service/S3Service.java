package com.example.awslambda.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final AmazonS3 s3Client;
    @Value("${aws.s3.bucket}")
    private String bucketName;

    @SneakyThrows
    public void uploadFile(MultipartFile file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, file.getOriginalFilename(),
            file.getInputStream(),
            new ObjectMetadata());

        s3Client.putObject(putObjectRequest);
    }

    @SneakyThrows
    public ResponseEntity<?> downloadFile(String fileName) {
        try {
            S3Object s3Object = s3Client.getObject(bucketName, fileName);
            byte[] content = s3Object.getObjectContent().readAllBytes();

            MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
            if ("jpg".equalsIgnoreCase(fileExtension)) {
                mediaType = MediaType.IMAGE_JPEG;
            } else if ("txt".equalsIgnoreCase(fileExtension)) {
                mediaType = MediaType.TEXT_PLAIN;
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(mediaType);
            headers.setContentDispositionFormData(fileName, fileName);

            return new ResponseEntity<>(content, headers, HttpStatus.OK);
        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND.value()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("File '%s' not found", fileName));
            } else {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }
}

