package com.example.csvprocessor.util;

import com.example.csvprocessor.model.CSVData;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@Component
public class CSVUtil {

    // Parses the CSV file from the input stream.
    public CSVData parseCSV(InputStream inputStream) throws IOException {
        try (Reader reader = new InputStreamReader(inputStream)) {
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim());
            CSVData csvData = new CSVData();
            csvData.setHeaders(new ArrayList<>(csvParser.getHeaderNames()));
            csvData.setRows(new ArrayList<>());
            for (CSVRecord record : csvParser) {
                List<String> row = new ArrayList<>();
                for (String header : csvParser.getHeaderNames()) {
                    row.add(record.get(header));
                }
                csvData.getRows().add(row);
            }
            return csvData;
        }
    }
}
