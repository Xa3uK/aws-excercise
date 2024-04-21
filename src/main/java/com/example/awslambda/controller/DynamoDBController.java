package com.example.awslambda.controller;

import com.example.awslambda.model.DataExample;
import com.example.awslambda.service.DynamoDBService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/dynamodb")
@Slf4j
public class DynamoDBController {
    private final DynamoDBService dynamoDBService;

    @PostMapping
    public void createData(@RequestBody DataExample dataExample) {

        dataExample.getInputData().forEach((key, value) -> log.info("Key: {}, value: {}", key, value));
        dynamoDBService.insertData(dataExample);
    }
//temp
    @GetMapping("{id}")
    public ResponseEntity<DataExample> getDataById(@PathVariable String id) {
        DataExample dataExample = dynamoDBService.getDataById(id);

        if (dataExample != null) {
            return ResponseEntity.ok(dataExample);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public List<DataExample> getAllData(){
        return dynamoDBService.getAllData();
    }

}
