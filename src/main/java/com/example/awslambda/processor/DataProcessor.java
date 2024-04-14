package com.example.awslambda.processor;

import com.example.awslambda.model.DataExample;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataProcessor {

    public File processRequest(DataExample dataExample) {
        Map<String, String> data = dataExample.getInputData();

        String filePath = "src/main/resources/output.txt";
        File file = new File(filePath);

        try (FileWriter writer = new FileWriter(file)) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                writer.write(entry.getKey() + ": " + entry.getValue() + "\n");
            }
            log.error("File created successfully at: {}", file.getAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to create file: {}", e.getMessage());
        }
        return file;
    }
}

