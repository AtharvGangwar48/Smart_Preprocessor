package com.example.csvprocessor.service;

import com.example.csvprocessor.model.CSVData;
import com.example.csvprocessor.model.OperationRequest;
import com.example.csvprocessor.util.CSVUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile; // Add this import
import java.io.IOException;
import java.util.*;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import java.util.stream.Collectors;

@Service
public class DataProcessingService {

    private final CSVUtil csvUtil;

    public DataProcessingService(CSVUtil csvUtil) {
        this.csvUtil = csvUtil;
    }

    // Parses the CSV file and returns a CSVData object.
    public CSVData parseCSV(MultipartFile file) throws IOException {
        return csvUtil.parseCSV(file.getInputStream());
    }

    // Processes the data according to the user's selections.
    public CSVData processData(CSVData csvData, OperationRequest operationRequest) {
        List<String> headers = csvData.getHeaders();
        List<List<String>> rows = csvData.getRows();
        List<Integer> selectedColumnIndices = getSelectedColumnIndices(headers, operationRequest.getSelectedColumns());

        // Create a new CSVData object to store the processed data.
        CSVData processedData = new CSVData();
        processedData.setHeaders(new ArrayList<>(headers)); // Initialize, don't just assign.
        processedData.setRows(new ArrayList<>());

        // If no columns are selected, return the original data.
        if (selectedColumnIndices.isEmpty()) {
            return csvData;
        }

        // Process each row
        for (List<String> row : rows) {
            List<String> processedRow = new ArrayList<>(row); // Start with original row
            for (Integer columnIndex : selectedColumnIndices) {
                processColumn(processedRow, columnIndex, operationRequest);
            }
            processedData.getRows().add(processedRow);
        }
        return processedData;
    }
    // Helper method to get the indices of the selected columns
    private List<Integer> getSelectedColumnIndices(List<String> headers, List<String> selectedColumns) {
        List<Integer> selectedColumnIndices = new ArrayList<>();
        if (selectedColumns != null) {
            for (String selectedColumn : selectedColumns) {
                int index = headers.indexOf(selectedColumn);
                if (index != -1) {
                    selectedColumnIndices.add(index);
                }
            }
        }
        return selectedColumnIndices;
    }

    private void processColumn(List<String> row, int columnIndex, OperationRequest operationRequest) {
        String value = row.get(columnIndex);

        // Handle missing values
        if (value == null || value.isEmpty()) {
            value = handleMissingValue(row, columnIndex, operationRequest.getMissingValueStrategy());
            row.set(columnIndex, value); // Update the row with the handled value
        }

        // Convert to double for numerical operations
        Double numericValue = tryConvertToDouble(value);
        if (numericValue != null) { // Only process if it's a number
            // Handle outliers
            numericValue = handleOutliers(row, columnIndex, operationRequest.getOutlierHandlingStrategy(), numericValue);

            // Normalize
            numericValue = normalizeData(numericValue, columnIndex, operationRequest.getNormalizationStrategy(), row);
            row.set(columnIndex, String.valueOf(numericValue));
        }
    }

    // Handles missing values based on the selected strategy.
    private String handleMissingValue(List<String> row, int columnIndex, String strategy) {
        if ("mean".equalsIgnoreCase(strategy)) {
            return String.valueOf(calculateMean(row, columnIndex));
        } else if ("median".equalsIgnoreCase(strategy)) {
            return String.valueOf(calculateMedian(row, columnIndex));
        } else if ("zero".equalsIgnoreCase(strategy)) {
            return "0";
        } else {
            return ""; // Or throw an exception, or use a default value
        }
    }

    // Removes outliers based on the selected strategy.
    private Double handleOutliers(List<String> row, int columnIndex, String strategy, double value) {
        if ("zscore".equalsIgnoreCase(strategy)) {
            double mean = calculateMean(row, columnIndex);
            double stdDev = calculateStdDev(row, columnIndex);
            double zScore = (value - mean) / stdDev;
            if (Math.abs(zScore) > 3) { // Example: Z-score threshold of 3
                return calculateMean(row, columnIndex); // Replace with mean
            }
        } else if ("iqr".equalsIgnoreCase(strategy)) {
            DescriptiveStatistics stats = getColumnStatistics(row, columnIndex);
            double q1 = stats.getPercentile(25);
            double q3 = stats.getPercentile(75);
            double iqr = q3 - q1;
            if (value < (q1 - 1.5 * iqr) || value > (q3 + 1.5 * iqr)) {
                return calculateMedian(row, columnIndex);
            }
        }
        return value;
    }

    // Normalizes the data based on the selected strategy.
    private Double normalizeData(double value, int columnIndex, String strategy, List<String> row) {
        if ("minmax".equalsIgnoreCase(strategy)) {
            double min = calculateMin(row, columnIndex);
            double max = calculateMax(row, columnIndex);
            if (max - min == 0) return 0.0; //handle division by zero
            return (value - min) / (max - min);
        } else if ("zscore".equalsIgnoreCase(strategy)) {
            double mean = calculateMean(row, columnIndex);
            double stdDev = calculateStdDev(row, columnIndex);
            if (stdDev == 0) return 0.0; //handle division by zero
            return (value - mean) / stdDev;
        }
        return value;
    }

    // Helper Methods for calculations
    // -----------------------------

    private Double tryConvertToDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // Calculate mean of a column
    private double calculateMean(List<String> row, int columnIndex) {
        List<Double> numericValues = getNumericColumnValues(row, columnIndex);
        if (numericValues.isEmpty()) return 0;
        double sum = 0;
        for (double value : numericValues) {
            sum += value;
        }
        return sum / numericValues.size();
    }

    // Calculate median of a column
    private double calculateMedian(List<String> row, int columnIndex) {
        List<Double> numericValues = getNumericColumnValues(row, columnIndex);
        if (numericValues.isEmpty()) return 0;
        Collections.sort(numericValues);
        int size = numericValues.size();
        if (size % 2 == 0) {
            return (numericValues.get(size / 2 - 1) + numericValues.get(size / 2)) / 2.0;
        } else {
            return numericValues.get(size / 2);
        }
    }

    // Calculate standard deviation of a column
    private double calculateStdDev(List<String> row, int columnIndex) {
        List<Double> numericValues = getNumericColumnValues(row, columnIndex);
        if (numericValues.isEmpty()) return 0;
        double mean = calculateMean(row, columnIndex);
        double sum = 0;
        for (double value : numericValues) {
            sum += Math.pow(value - mean, 2);
        }
        return Math.sqrt(sum / numericValues.size());
    }

    // Calculate min of a column
    private double calculateMin(List<String> row, int columnIndex) {
        List<Double> numericValues = getNumericColumnValues(row, columnIndex);
        if (numericValues.isEmpty()) return 0;
        double min = numericValues.get(0);
        for(int i=1; i< numericValues.size(); i++){
            if(numericValues.get(i) < min)
                min = numericValues.get(i);
        }
        return min;
    }

    // Calculate max of a column
    private double calculateMax(List<String> row, int columnIndex) {
        List<Double> numericValues = getNumericColumnValues(row, columnIndex);
        if (numericValues.isEmpty()) return 0;
        double max = numericValues.get(0);
        for(int i=1; i< numericValues.size(); i++){
            if(numericValues.get(i) > max)
                max = numericValues.get(i);
        }
        return max;
    }

    private List<Double> getNumericColumnValues(List<String> row, int columnIndex) {
        List<Double> numericValues = new ArrayList<>();
        for (String value : row) {
            Double numericValue = tryConvertToDouble(value);
            if (numericValue != null) {
                numericValues.add(numericValue);
            }
        }
        return numericValues;
    }

    private DescriptiveStatistics getColumnStatistics(List<String> row, int columnIndex) {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        List<Double> numericValues = getNumericColumnValues(row, columnIndex);
        numericValues.forEach(stats::addValue);
        return stats;
    }
}