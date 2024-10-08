package com.crud.demo.controller;

import com.crud.demo.model.StatisticsFinal;
import com.crud.demo.service.StatisticsFinalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class FileUploadController {

    private List<String> allLogs;
    private List<String> detailedErrorLogs;
    private List<String> downloadedFilenames = new ArrayList<>();

    @Autowired
    private StatisticsFinalService statisticsFinalService;

    @Autowired
    private HttpSession httpSession;

    @GetMapping("/")
    public String index() {
        return "login";
    }

    @GetMapping("/webpage")
    public String webpage(Model model) {
        return "webpage";
    }

    @PostMapping("/upload")
    public String uploadLogFile(@RequestParam("logfile") MultipartFile logFile, Model model) {
        Long userId = getCurrentUserId();

        if (logFile.isEmpty()) {
            model.addAttribute("error", "Please select a file to upload.");
            return "webpage";
        }

        String uploadedFileName = logFile.getOriginalFilename();
        StringBuilder downloadedExceptions = new StringBuilder(); // Use StringBuilder for better performance

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(logFile.getInputStream()))) {
            List<String> logLines = reader.lines().collect(Collectors.toList());

            Map<String, Integer> counts = countLogOccurrences(logLines);
            Map<String, Integer> filteredCounts = counts.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            allLogs = logLines;
            detailedErrorLogs = extractDetailedErrorLogs(logLines);

            model.addAttribute("counts", filteredCounts);
            model.addAttribute("allLogs", allLogs);
            model.addAttribute("detailedErrorLogs", detailedErrorLogs);

         // Capture the exceptions downloaded
            Set<String> uniqueDownloadedExceptions = new HashSet<>(); // Use a Set for uniqueness
            for (String exceptionType : filteredCounts.keySet()) {
                if (filteredCounts.get(exceptionType) > 0) {
                    uniqueDownloadedExceptions.add(exceptionType); // Only add unique exception types
                }
            }

            // Convert Set to String for saving
            String downloadedExceptionsStr = String.join(", ", uniqueDownloadedExceptions);


            // Save statistics with the downloaded exceptions captured
            statisticsFinalService.saveStatistics(
                userId,
                uploadedFileName,
                null, // File name will be generated when downloading logs
                filteredCounts.getOrDefault("AccessException", 0),
                filteredCounts.getOrDefault("CloudClientException", 0),
                filteredCounts.getOrDefault("InvalidFormatException", 0),
                filteredCounts.getOrDefault("NullPointerException", 0),
                filteredCounts.getOrDefault("SchedulerException", 0),
                filteredCounts.getOrDefault("SuperCsvException", 0),
                filteredCounts.keySet().toString(),
                "Uploaded", // Status updated to indicate upload
                downloadedExceptions.toString() // Save the downloaded exceptions here
            );

        } catch (IOException e) {
            model.addAttribute("error", "Failed to process the file: " + e.getMessage());
        }

        return "webpage";
    }

    private Long getCurrentUserId() {
        return (Long) httpSession.getAttribute("userId");
    }

    private Map<String, Integer> countLogOccurrences(List<String> logLines) {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("ERROR", countOccurrences(logLines, "ERROR"));
        counts.put("INFO", countOccurrences(logLines, "INFO"));
        counts.put("DEBUG", countOccurrences(logLines, "DEBUG"));
        counts.put("NullPointerException", countOccurrences(logLines, "NullPointerException"));
        counts.put("SchedulerException", countOccurrences(logLines, "SchedulerException"));
        counts.put("AccessException", countOccurrences(logLines, "AccessException"));
        counts.put("InvalidFormatException", countOccurrences(logLines, "InvalidFormatException"));
        counts.put("CloudClientException", countOccurrences(logLines, "CloudClientException"));
        counts.put("ValidationException", countOccurrences(logLines, "ValidationException"));
        counts.put("SuperCsvException", countOccurrences(logLines, "SuperCsvException"));
        return counts;
    }

    @GetMapping("/downloadErrorLogs")
    public ResponseEntity<InputStreamResource> downloadLogs(Model model) throws IOException {
        if (detailedErrorLogs == null) {
            return ResponseEntity.badRequest().body(null);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Detailed Error Logs:\n").append(String.join("\n", detailedErrorLogs)).append("\n\n");

        ByteArrayInputStream in = new ByteArrayInputStream(sb.toString().getBytes());
        InputStreamResource resource = new InputStreamResource(in);

        String resultingFileName = generateResultingFileName("DetailedErrorLogs", "");

        downloadedFilenames.add(resultingFileName);
        
        // Save the resulting file name
        saveResultingFileName(resultingFileName, "Downloaded");
        saveDownloadedException(resultingFileName, "Detailed Error Logs");

        
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + resultingFileName)
            .contentType(MediaType.TEXT_PLAIN)
            .body(resource);
    }

    @GetMapping("/downloadFilteredErrorLogs")
    public ResponseEntity<InputStreamResource> downloadFilteredLogs(@RequestParam("exceptionType") String exceptionType) throws IOException {
        if (detailedErrorLogs == null || exceptionType == null || exceptionType.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        List<String> filteredLogs = detailedErrorLogs.stream()
            .filter(stackTrace -> stackTrace.contains(exceptionType))
            .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        sb.append("Filtered Error Logs for ").append(exceptionType).append(":\n")
          .append(String.join("\n", filteredLogs)).append("\n\n");

        ByteArrayInputStream in = new ByteArrayInputStream(sb.toString().getBytes());
        InputStreamResource resource = new InputStreamResource(in);

        // Generate the resulting file name with the respective exception name
        String resultingFileName = generateResultingFileName(exceptionType, "FilteredLogs");

        // Store the downloaded filename
        downloadedFilenames.add(resultingFileName);

        // Save the resulting file name to the database
        saveResultingFileName(resultingFileName, "Filtered & Downloaded ");
        // Save the resulting file name and exception type to the downloadedException field in the database
        saveDownloadedException(resultingFileName, exceptionType);

        // Set the file name dynamically in the response header
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + resultingFileName)
            .contentType(MediaType.TEXT_PLAIN)
            .body(resource);
    }

    @GetMapping("/filteredErrorLogs")
    @ResponseBody
    public List<String> filteredErrorLogs(@RequestParam("exceptionType") String exceptionType) {
        if (detailedErrorLogs == null || exceptionType == null || exceptionType.isEmpty()) {
            return Collections.emptyList();
        }

        return detailedErrorLogs.stream()
            .filter(stackTrace -> stackTrace.contains(exceptionType))
            .collect(Collectors.toList());
    }

    private int countOccurrences(List<String> logLines, String keyword) {
        return (int) logLines.stream().filter(line -> line.contains(keyword)).count();
    }

    private List<String> extractDetailedErrorLogs(List<String> logLines) {
        List<String> detailedLogs = new ArrayList<>();
        boolean isCapturing = false;
        StringBuilder currentStackTrace = new StringBuilder();

        for (String line : logLines) {
            if (line.contains("ERROR")) {
                isCapturing = true;
                currentStackTrace = new StringBuilder();
                currentStackTrace.append(line).append("\n");
            } else if (isCapturing) {
                if (line.isEmpty() || line.startsWith("INFO") || line.startsWith("DEBUG")) {
                    detailedLogs.add(currentStackTrace.toString().trim());
                    isCapturing = false;
                } else {
                    currentStackTrace.append(line).append("\n");
                }
            }
        }

        if (isCapturing) {
            detailedLogs.add(currentStackTrace.toString().trim());
        }

        return detailedLogs;
    }

    private String generateResultingFileName(String prefix, String suffix) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss");
        String timestamp = LocalDateTime.now().format(formatter);
        return prefix + "_" + timestamp + (suffix.isEmpty() ? "" : "_" + suffix) + ".txt";
    }

    private void saveResultingFileName(String filename, String status) {
        Long userId = getCurrentUserId();
        // Update the statistics with the resulting file name and status
        statisticsFinalService.updateResultingFileName(userId, filename, status);
    }
    private void saveDownloadedException(String filename, String exceptionType) {
        Long userId = getCurrentUserId();
        // Update the statistics with the resulting file name and the exception type as "Downloaded Exception"
        statisticsFinalService.updateDownloadedException(userId, filename, exceptionType);
    }

    @GetMapping("/statistics")
    public String statistics(Model model) {
        Long userId = getCurrentUserId();
        List<StatisticsFinal> statisticsList = statisticsFinalService.getStatisticsByUserId(userId);
        model.addAttribute("statistics", statisticsList);
        return "statistics";
    }
    @GetMapping("/api/statistics")
    @ResponseBody
    public List<StatisticsFinal> getFilteredStatistics(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        
        Long userId = getCurrentUserId(); // Assuming this gets the current user ID
        
        LocalDateTime fromDate = null;
        LocalDateTime toDate = null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (from != null && !from.isEmpty()) {
            fromDate = LocalDate.parse(from, formatter).atStartOfDay(); // Start of the day
        }

        if (to != null && !to.isEmpty()) {
            toDate = LocalDate.parse(to, formatter).atTime(23, 59, 59); // End of the day
        }

        if (fromDate != null && toDate != null) {
            return statisticsFinalService.getStatisticsByDateRange(userId, fromDate, toDate);
        }

        return statisticsFinalService.getStatisticsByUserId(userId); // If no date provided, return all data
    }
}
