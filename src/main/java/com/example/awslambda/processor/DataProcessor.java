package com.example.awslambda.processor;

import com.example.awslambda.model.InputData;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class DataProcessor {

    public void processRequest(InputData inputData) {
        Map<String, String> data = inputData.getInputData();

        String filePath = "src/main/resources/output.txt";
        File file = new File(filePath);

        try (FileWriter writer = new FileWriter(file)) {
            // Write the data to the file
            for (Map.Entry<String, String> entry : data.entrySet()) {
                writer.write(entry.getKey() + ": " + entry.getValue() + "\n");
            }
            System.out.println("File created successfully at: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to create file: " + e.getMessage());
        }
    }
}

