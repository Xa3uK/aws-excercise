package com.example.awslambda.service;

import com.example.awslambda.model.InputData;
import com.example.awslambda.processor.DataProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FileService {

  private final DataProcessor dataProcessor;

  public void processData(InputData inputData){
      dataProcessor.processRequest(inputData);
  }

}
