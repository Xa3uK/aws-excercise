package com.example.awslambda.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.MediaType;

@Data
@AllArgsConstructor
public class FileResponse {

    private byte[] content;
    private MediaType mediaType;
}
