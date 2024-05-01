package com.example.awslambda.controller;

import com.example.awslambda.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    private final S3Service s3Service;

    @PostMapping()
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        s3Service.uploadFile(file);

        return "File uploaded successfully to S3!";
    }

    @GetMapping
    public ResponseEntity<byte[]> getFile(@RequestParam String fileName){
        return s3Service.downloadFile(fileName);
    }
}
