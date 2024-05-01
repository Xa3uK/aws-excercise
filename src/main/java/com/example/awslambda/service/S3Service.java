package com.example.awslambda.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.example.awslambda.model.FileResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    public FileResponse getFileByName(String fileName) throws IOException {
        S3Object s3Object;
        try {
            s3Object = s3Client.getObject(bucketName, fileName);
        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND.value()) {
                throw new FileNotFoundException("File '%s' not found".formatted(fileName));
            } else {
                throw e;
            }
        }

        byte[] content = s3Object.getObjectContent().readAllBytes();

        MediaType mediaType;
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        if ("jpg".equalsIgnoreCase(fileExtension) || "jpeg".equalsIgnoreCase(fileExtension)) {
            mediaType = MediaType.IMAGE_JPEG;
        } else if ("txt".equalsIgnoreCase(fileExtension)) {
            mediaType = MediaType.TEXT_PLAIN;
        } else if ("png".equalsIgnoreCase(fileExtension)){
            mediaType = MediaType.IMAGE_PNG;
        } else {
            throw new UnsupportedOperationException("MediaType not supported");
        }

        return new FileResponse(content, mediaType);
    }
}

