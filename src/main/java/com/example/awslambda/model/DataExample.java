package com.example.awslambda.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataExample {

    private String id;

    @JsonProperty("input_data")
    private Map<String, String> inputData;
}
