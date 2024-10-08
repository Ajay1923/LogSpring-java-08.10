package com.crud.demo.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "statistics")
public class StatisticsFinal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  

    private Long userId;  
    private LocalDateTime timestamp;
    private String uploadedFileName;
    private String resultingFileName;
    private String logCodes;
    private String status;
    private String downloadedException;
    private int accessExceptionCount;
    private int cloudClientExceptionCount;
    private int invalidFormatExceptionCount;
    private int nullPointerExceptionCount;
    private int schedulerExceptionCount;
    private int superCsvExceptionCount;

    // Default constructor
    public StatisticsFinal() {}

    // Constructor to initialize essential fields
    public StatisticsFinal(Long userId, LocalDateTime timestamp, String uploadedFileName, String resultingFileName, String status) {
        this.userId = userId;
        this.timestamp = timestamp;
        this.uploadedFileName = uploadedFileName;
        this.resultingFileName = resultingFileName;
        this.status = status;
    }


    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getUploadedFileName() {
        return uploadedFileName;
    }

    public void setUploadedFileName(String uploadedFileName) {
        this.uploadedFileName = uploadedFileName;
    }

    public String getResultingFileName() {
        return resultingFileName;
    }

    public void setResultingFileName(String resultingFileName) {
        this.resultingFileName = resultingFileName;
    }

    public String getLogCodes() {
        return logCodes;
    }

    public void setLogCodes(String logCodes) {
        this.logCodes = logCodes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDownloadedException() {
        return downloadedException;
    }
    
    public void setDownloadedException(String downloadedException) {
        this.downloadedException = downloadedException;
    }

    public int getAccessExceptionCount() {
        return accessExceptionCount;
    }

    public void setAccessExceptionCount(int accessExceptionCount) {
        this.accessExceptionCount = accessExceptionCount;
    }

    public int getCloudClientExceptionCount() {
        return cloudClientExceptionCount;
    }

    public void setCloudClientExceptionCount(int cloudClientExceptionCount) {
        this.cloudClientExceptionCount = cloudClientExceptionCount;
    }

    public int getInvalidFormatExceptionCount() {
        return invalidFormatExceptionCount;
    }

    public void setInvalidFormatExceptionCount(int invalidFormatExceptionCount) {
        this.invalidFormatExceptionCount = invalidFormatExceptionCount;
    }

    public int getNullPointerExceptionCount() {
        return nullPointerExceptionCount;
    }

    public void setNullPointerExceptionCount(int nullPointerExceptionCount) {
        this.nullPointerExceptionCount = nullPointerExceptionCount;
    }

    public int getSchedulerExceptionCount() {
        return schedulerExceptionCount;
    }

    public void setSchedulerExceptionCount(int schedulerExceptionCount) {
        this.schedulerExceptionCount = schedulerExceptionCount;
    }

    public int getSuperCsvExceptionCount() {
        return superCsvExceptionCount;
    }

    public void setSuperCsvExceptionCount(int superCsvExceptionCount) {
        this.superCsvExceptionCount = superCsvExceptionCount;
    }

    @Override
    public String toString() {
        return "StatisticsFinal{" +
                "id=" + id +
                ", userId=" + userId +
                ", timestamp=" + timestamp +
                ", uploadedFileName='" + uploadedFileName + '\'' +
                ", resultingFileName='" + resultingFileName + '\'' +
                ", logCodes='" + logCodes + '\'' +
                ", status='" + status + '\'' +
                ", downloadedException='" + downloadedException + '\'' +
                ", accessExceptionCount=" + accessExceptionCount +
                ", cloudClientExceptionCount=" + cloudClientExceptionCount +
                ", invalidFormatExceptionCount=" + invalidFormatExceptionCount +
                ", nullPointerExceptionCount=" + nullPointerExceptionCount +
                ", schedulerExceptionCount=" + schedulerExceptionCount +
                ", superCsvExceptionCount=" + superCsvExceptionCount +
                '}';
    }
}
