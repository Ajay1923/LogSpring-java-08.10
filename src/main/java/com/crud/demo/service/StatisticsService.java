package com.crud.demo.service;

import org.springframework.stereotype.Service;
import com.crud.demo.model.StatisticsEntry;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticsService {
    
    private List<StatisticsEntry> statisticsEntries = new ArrayList<>();

    public void addStatisticsEntry(StatisticsEntry entry) {
        statisticsEntries.add(entry);
    }

    public void saveDownloadedFileName(String fileName) {
        if (!statisticsEntries.isEmpty()) {
            StatisticsEntry lastEntry = statisticsEntries.get(statisticsEntries.size() - 1);
            lastEntry.setDownloadedException(fileName);
        }
    }

    public List<StatisticsEntry> getStatisticsEntries() {
        return statisticsEntries;
    }
}
