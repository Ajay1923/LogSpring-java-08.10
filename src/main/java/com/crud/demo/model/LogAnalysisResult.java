package com.crud.demo.model;

import java.util.Map;

public class LogAnalysisResult {
    private int errorCount;
    private int infoCount;
    private int debugCount;
    private Map<String, Integer> exceptionCounts;

   
    public LogAnalysisResult(int errorCount, int infoCount, int debugCount, Map<String, Integer> exceptionCounts) {
        this.errorCount = errorCount;
        this.infoCount = infoCount;
        this.debugCount = debugCount;
        this.exceptionCounts = exceptionCounts;
    }

    
    public int getErrorCount() {
        return errorCount;
    }

    public int getInfoCount() {
        return infoCount;
    }

    public int getDebugCount() {
        return debugCount;
    }

    public Map<String, Integer> getExceptionCounts() {
        return exceptionCounts;
    }

   
    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public void setInfoCount(int infoCount) {
        this.infoCount = infoCount;
    }

    public void setDebugCount(int debugCount) {
        this.debugCount = debugCount;
    }

    public void setExceptionCounts(Map<String, Integer> exceptionCounts) {
        this.exceptionCounts = exceptionCounts;
    }
}
