package com.example.awslambda.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Data;

@Data
public class InputData {

    @JsonProperty("input_data")
    Map<String, String> inputData;
}
