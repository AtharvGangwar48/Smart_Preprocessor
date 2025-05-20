package com.example.csvprocessor.model;

import lombok.Data;
import java.util.List;

@Data
public class OperationRequest {
    private List<String> selectedColumns;
    private String missingValueStrategy;
    private String outlierHandlingStrategy;
    private String normalizationStrategy;
}
