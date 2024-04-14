package com.example.awslambda.service;

import com.example.awslambda.model.DataExample;
import com.example.awslambda.processor.DataProcessor;
import java.io.File;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FileService {

  private final DataProcessor dataProcessor;
  private final S3UploaderService s3UploaderService;
  @Value("${aws.s3.bucket}")
  private String bucket;

  public void processData(DataExample dataExample){
    File file =  dataProcessor.processRequest(dataExample);
      s3UploaderService.uploadFileToS3(file,bucket, file.getName());
  }

}
