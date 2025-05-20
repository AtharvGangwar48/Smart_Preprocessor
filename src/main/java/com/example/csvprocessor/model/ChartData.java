package com.example.csvprocessor.model;

import org.springframework.data.annotation.Id;
import java.util.List;

public class ChartData {
    @Id
    private String id;
    private String chartTitle;
    private List<String> labels;
    private List<Dataset> datasets;

    public static class Dataset {
        private String label;
        private List<String> data;
        private String backgroundColor;
        private String borderColor;
        private int borderWidth = 1;

        // Constructors, Getters, and Setters
        public Dataset() {
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public List<String> getData() {
            return data;
        }

        public void setData(List<String> data) {
            this.data = data;
        }

        public String getBackgroundColor() {
            return backgroundColor;
        }

        public void setBackgroundColor(String backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        public String getBorderColor() {
            return borderColor;
        }

        public void setBorderColor(String borderColor) {
            this.borderColor = borderColor;
        }

        public int getBorderWidth() {
            return borderWidth;
        }

        public void setBorderWidth(int borderWidth) {
            this.borderWidth = borderWidth;
        }
    }

    // Constructors, Getters, and Setters for ChartData
    public ChartData() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChartTitle() {
        return chartTitle;
    }

    public void setChartTitle(String chartTitle) {
        this.chartTitle = chartTitle;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<Dataset> getDatasets() {
        return datasets;
    }

    public void setDatasets(List<Dataset> datasets) {
        this.datasets = datasets;
    }
}