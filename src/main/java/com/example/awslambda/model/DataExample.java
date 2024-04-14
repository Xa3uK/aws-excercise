package com.example.awslambda.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Data;

@Data
public class DataExample {

    private String id;

    @JsonProperty("input_data")
    private Map<String, String> inputData;
}
