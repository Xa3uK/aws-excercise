package com.example.awslambda.controller;

import com.example.awslambda.model.InputData;
import com.example.awslambda.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
public class FileController {

    private final FileService fileService;

    @PostMapping
    public void post(@RequestBody InputData inputData) {

        inputData.getInputData().forEach((key, value) -> log.info("Key: {}, value:{}", key, value));
        fileService.processData(inputData);
    }

}
