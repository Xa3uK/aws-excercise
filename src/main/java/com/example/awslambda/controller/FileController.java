package com.example.awslambda.controller;

import com.example.awslambda.model.InputData;
import com.example.awslambda.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class FileController {

    private final FileService fileService;

    @PostMapping
    public void post(@RequestBody InputData inputData){

        inputData.getInputData().entrySet().forEach(System.out::println);
        fileService.processData(inputData);
    }

}
