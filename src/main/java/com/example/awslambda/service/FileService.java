package com.example.awslambda.service;

import com.example.awslambda.model.InputData;
import com.example.awslambda.processor.DataProcessor;
import com.example.awslambda.uploader.S3Uploader;
import java.io.File;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FileService {

  private final DataProcessor dataProcessor;
  private final S3Uploader s3Uploader;

  public void processData(InputData inputData){
    File file =  dataProcessor.processRequest(inputData);
      s3Uploader.uploadFileToS3(file,"aws-course-my-rd", file.getName());
  }

}
