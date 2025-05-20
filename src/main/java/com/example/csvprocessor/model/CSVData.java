package com.example.csvprocessor.model;

import lombok.Data;
import java.util.List;

@Data
public class CSVData {
    private List<String> headers;
    private List<List<String>> rows;
}
