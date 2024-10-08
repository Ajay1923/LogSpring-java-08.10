package com.crud.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LogFileService {

    private static final Logger logger = LoggerFactory.getLogger(LogFileService.class);

    // Variables to store logs
    private List<String> allLogs = new ArrayList<>();
    private List<String> errorLogs = new ArrayList<>();

    public Map<String, Integer> processLogFile(MultipartFile file) throws IOException {
        logger.info("Starting log file processing.");

        Map<String, Integer> counts = new HashMap<>();
        int errorCount = 0, infoCount = 0, debugCount = 0;
        int nullPointerExceptionCount = 0, schedulerExceptionCount = 0, accessExceptionCount = 0;
        int invalidFormatExceptionCount = 0, cloudClientExceptionCount = 0, validationExceptionCount = 0;
        int superCsvExceptionCount = 0, others = 0;

        allLogs.clear();
        errorLogs.clear();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                allLogs.add(line); // Store all log lines

                if (line.startsWith("ERROR")) {
                    errorCount++;
                    errorLogs.add(line); // Store only error log lines
                    String nextLine = reader.readLine();
                    if (nextLine != null && nextLine.contains("Exception")) {
                        errorLogs.add(nextLine); // Add the exception detail to error logs
                        if (nextLine.contains("NullPointerException")) {
                            nullPointerExceptionCount++;
                        } else if (nextLine.contains("SchedulerException")) {
                            schedulerExceptionCount++;
                        } else if (nextLine.contains("AccessException")) {
                            accessExceptionCount++;
                        } else if (nextLine.contains("InvalidFormatException")) {
                            invalidFormatExceptionCount++;
                        } else if (nextLine.contains("CloudClientException")) {
                            cloudClientExceptionCount++;
                        } else if (nextLine.contains("ValidationException")) {
                            validationExceptionCount++;
                        } else if (nextLine.contains("SuperCsvException")) {
                            superCsvExceptionCount++;
                        } else {
                            others++;
                        }
                    } else {
                        others++;
                    }
                } else if (line.startsWith("INFO")) {
                    infoCount++;
                } else if (line.startsWith("DEBUG")) {
                    debugCount++;
                }
            }
        }

        // Populate counts map
        counts.put("ERROR", errorCount);
        counts.put("INFO", infoCount);
        counts.put("DEBUG", debugCount);
        counts.put("NullPointerException", nullPointerExceptionCount);
        counts.put("SchedulerException", schedulerExceptionCount);
        counts.put("AccessException", accessExceptionCount);
        counts.put("InvalidFormatException", invalidFormatExceptionCount);
        counts.put("CloudClientException", cloudClientExceptionCount);
        counts.put("ValidationException", validationExceptionCount);
        counts.put("SuperCsvException", superCsvExceptionCount);
        counts.put("Other Exceptions", others);

        logger.info("Log file processing completed with results: {}", counts);
        return counts;
    }

    // Methods to get the logs
    public List<String> getAllLogs() {
        return allLogs;
    }

    public List<String> getErrorLogs() {
        return errorLogs;
    }
}
