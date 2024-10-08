package com.crud.demo.service;

import com.crud.demo.model.StatisticsFinal;
import com.crud.demo.repository.StatisticsFinalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatisticsFinalService {

    @Autowired
    private StatisticsFinalRepository statisticsFinalRepository;

    public void saveStatistics(Long userId, String uploadedFileName, String resultingFileName,
                               int accessExceptionCount, int cloudClientExceptionCount,
                               int invalidFormatExceptionCount, int nullPointerExceptionCount,
                               int schedulerExceptionCount, int superCsvExceptionCount,
                               String logCodes, String status, String downloadedException) {
        StatisticsFinal stats = new StatisticsFinal();
        stats.setUserId(userId);
        stats.setTimestamp(LocalDateTime.now());
        stats.setUploadedFileName(uploadedFileName);
        stats.setResultingFileName(resultingFileName);
        stats.setLogCodes(logCodes);
        stats.setStatus(status);
        stats.setDownloadedException(downloadedException);
        stats.setAccessExceptionCount(accessExceptionCount);
        stats.setCloudClientExceptionCount(cloudClientExceptionCount);
        stats.setInvalidFormatExceptionCount(invalidFormatExceptionCount);
        stats.setNullPointerExceptionCount(nullPointerExceptionCount);
        stats.setSchedulerExceptionCount(schedulerExceptionCount);
        stats.setSuperCsvExceptionCount(superCsvExceptionCount);

        statisticsFinalRepository.save(stats);
    }

    // Fetch statistics by user ID
    public List<StatisticsFinal> getStatisticsByUserId(Long userId) {
        return statisticsFinalRepository.findAllByUserId(userId);
    }
    
    // Update the resulting file name and the status
    public void updateResultingFileName(Long userId, String resultingFileName, String status) {
        StatisticsFinal statistics = findLatestStatisticsByUserId(userId);
        if (statistics != null) {
            statistics.setResultingFileName(resultingFileName);
            statistics.setStatus(status);
            statisticsFinalRepository.save(statistics);
        } else {
            System.out.println("No statistics found for userId: " + userId);
        }
    }
    
    public List<StatisticsFinal> getStatisticsByDateRange(Long userId, LocalDateTime fromDate, LocalDateTime toDate) {
        return statisticsFinalRepository.findAllByUserIdAndTimestampBetween(userId, fromDate, toDate);
    }


    // Update the downloadedException field based on the userId, filename, and exception type
    public void updateDownloadedException(Long userId, String filename, String exceptionType) {
        StatisticsFinal statistics = findLatestStatisticsByUserId(userId);
        if (statistics != null) {
            // Append the new exceptionType to the existing downloadedException value, if any
            String existingDownloadedException = statistics.getDownloadedException();
            if (existingDownloadedException == null || existingDownloadedException.isEmpty()) {
                statistics.setDownloadedException(exceptionType);
            } else {
                statistics.setDownloadedException(existingDownloadedException + ", " + exceptionType);
            }

            // Set the resulting file name
            statistics.setResultingFileName(filename);

            statisticsFinalRepository.save(statistics);
        } else {
            System.out.println("No statistics found for userId: " + userId);
        }
    }

    public StatisticsFinal findLatestStatisticsByUserId(Long userId) {
        return statisticsFinalRepository.findTopByUserIdOrderByIdDesc(userId);
    }
}
