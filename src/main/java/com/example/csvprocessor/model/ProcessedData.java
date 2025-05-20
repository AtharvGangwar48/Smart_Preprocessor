package com.example.csvprocessor.model;

import org.springframework.data.annotation.Id;
import java.util.Map;

public class ProcessedData {
    @Id
    private String id;
    private Map<String, String> data; // Represents a row of data as key-value pairs

    public ProcessedData() {
    }

    public ProcessedData(Map<String, String> data) {
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
