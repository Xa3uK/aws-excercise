package com.example.awslambda.controller;

import com.example.awslambda.model.DataExample;
import com.example.awslambda.service.DynamoDBService;
import com.example.awslambda.service.FileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
public class MainController {

    private final FileService fileService;
    private final DynamoDBService dynamoDBService;

    @PostMapping("/s3")
    public void posts3(@RequestBody DataExample dataExample) {

        dataExample.getInputData().forEach((key, value) -> log.info("Key: {}, value: {}", key, value));
        fileService.processData(dataExample);
    }

    @PostMapping("/dynamodb")
    public void postDynamoDb(@RequestBody DataExample dataExample) {

        dataExample.getInputData().forEach((key, value) -> log.info("Key: {}, value: {}", key, value));
        dynamoDBService.insertData(dataExample);
    }

    @GetMapping("/dynamodb/{id}")
    public ResponseEntity<DataExample> getById(@PathVariable String id) {
        DataExample dataExample = dynamoDBService.getDataById(id);

        if (dataExample != null) {
            return ResponseEntity.ok(dataExample);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
