package com.example.awslambda.controller;

import com.example.awslambda.model.DataExample;
import com.example.awslambda.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/s3")
@Slf4j
public class S3Controller {

    private final FileService fileService;

    @PostMapping()
    public void createFileInS3(@RequestBody DataExample dataExample) {

        dataExample.getInputData().forEach((key, value) -> log.info("Key: {}, value: {}", key, value));
        fileService.processData(dataExample);
    }

    @GetMapping
    public ResponseEntity<String> getFileInfo(@RequestParam String filename) {
        return fileService.getFileByName(filename);
    }
}
