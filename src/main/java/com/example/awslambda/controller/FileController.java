package com.example.awslambda.controller;

import com.example.awslambda.model.FileResponse;
import com.example.awslambda.service.S3Service;
import java.io.FileNotFoundException;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    private final S3Service s3Service;

    @PostMapping()
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        s3Service.uploadFile(file);

        return "File '%s'uploaded successfully to S3!".formatted(file.getOriginalFilename());
    }

    @GetMapping
    public ResponseEntity<?> getFile(@RequestParam String fileName) {
        try {
            FileResponse fileResponse = s3Service.getFileByName(fileName);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(fileResponse.getMediaType());
            return new ResponseEntity<>(fileResponse.getContent(), httpHeaders, HttpStatus.OK);
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
