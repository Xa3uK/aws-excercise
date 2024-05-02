package com.example.awslambda.controller;

import com.example.awslambda.model.FileResponse;
import com.example.awslambda.service.S3Service;
import java.io.FileNotFoundException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {

    private final S3Service s3Service;

    @PostMapping()
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        s3Service.uploadFile(file);
        return ResponseEntity.ok("File '%s'uploaded successfully to S3!".formatted(file.getOriginalFilename()));
    }

    @GetMapping
    public ResponseEntity<?> getFile(@RequestParam String fileName) throws IOException {
        FileResponse fileResponse = s3Service.getFileByName(fileName);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(fileResponse.getMediaType());
        return new ResponseEntity<>(fileResponse.getContent(), httpHeaders, HttpStatus.OK);
    }
}
