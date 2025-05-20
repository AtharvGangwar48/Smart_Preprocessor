package com.example.csvprocessor.service;

import com.example.csvprocessor.model.CSVData;
import com.example.csvprocessor.model.ChartData; // New model class
import com.example.csvprocessor.repository.ChartDataRepository; // New repository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class MongoDBService {

    private final ChartDataRepository chartDataRepository;

    @Autowired
    public MongoDBService(ChartDataRepository chartDataRepository) {
        this.chartDataRepository = chartDataRepository;
    }

    public void saveDataForCharts(CSVData csvData, String chartTitle) {
        ChartData chartDocument = new ChartData();
        chartDocument.setChartTitle(chartTitle);
        chartDocument.setLabels(new ArrayList<>(csvData.getHeaders()));
        List<ChartData.Dataset> datasets = new ArrayList<>();

        List<String> headers = csvData.getHeaders();
        List<List<String>> rows = csvData.getRows();

        for (int i = 0; i < headers.size(); i++) {
            ChartData.Dataset dataset = new ChartData.Dataset();
            dataset.setLabel(headers.get(i));
            List<String> columnData = new ArrayList<>();
            for (List<String> row : rows) {
                if (i < row.size()) {
                    columnData.add(row.get(i));
                } else {
                    columnData.add(null); // Or some default value for missing data
                }
            }
            dataset.setData(columnData);
            // You can add logic here to set different background/border colors for each dataset
            datasets.add(dataset);
        }

        chartDocument.setDatasets(datasets);
        chartDataRepository.save(chartDocument);
    }
}