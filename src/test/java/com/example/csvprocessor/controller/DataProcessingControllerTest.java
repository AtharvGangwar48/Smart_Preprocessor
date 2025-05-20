package com.example.csvprocessor.controller;

import com.example.csvprocessor.model.CSVData;
import com.example.csvprocessor.model.HeaderColumn;
import com.example.csvprocessor.model.OperationRequest;
import com.example.csvprocessor.service.DataProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class DataProcessingControllerTest {

    @Mock
    private DataProcessingService dataProcessingService;
    @Mock
    private MongoDBService mongoDBService;
    @InjectMocks
    private DataProcessingController dataProcessingController;
    @Mock
    private Model model;

    private RedirectAttributes redirectAttributes;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        redirectAttributes = new RedirectAttributesModelMap();
    }

    @Test
    public void testIndex() {
        assertEquals("upload", dataProcessingController.index());
    }

    @Test
    public void testHandleFileUpload_emptyFile() {
        MultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);
        String result = dataProcessingController.handleFileUpload(emptyFile, redirectAttributes, model);
        assertEquals("upload", result);
        verify(model, times(1)).addAttribute("message", "Please select a file to upload");
    }

    @Test
    public void testHandleFileUpload_validFile() throws IOException {
        MultipartFile validFile = new MockMultipartFile("file", "test.csv", "text/csv", "header1,header2\nvalue1,value2".getBytes());
        CSVData csvData = new CSVData();
        List<String> headers = new ArrayList<>();
        headers.add("header1");
        headers.add("header2");
        csvData.setHeaders(headers);
        List<HeaderColumn> headerColumns = new ArrayList<>();
        headerColumns.add(new HeaderColumn("header1"));
        headerColumns.add(new HeaderColumn("header2"));

        when(dataProcessingService.parseCSV(validFile)).thenReturn(csvData);

        String result = dataProcessingController.handleFileUpload(validFile, redirectAttributes, model);

        assertEquals("process", result);
        verify(dataProcessingService, times(1)).parseCSV(validFile);
        verify(model, times(1)).addAttribute("csvData", csvData);
        verify(model, times(1)).addAttribute("headerColumns", headerColumns);
    }

    @Test
    public void testProcessData_validRequest() {
        OperationRequest operationRequest = new OperationRequest();
        operationRequest.setSelectedColumns(List.of("header1"));
        operationRequest.setMissingValueStrategy("mean");
        operationRequest.setOutlierHandlingStrategy("zscore");
        operationRequest.setNormalizationStrategy("minmax");

        CSVData csvData = new CSVData();
        List<String> headers = new ArrayList<>();
        headers.add("header1");
        headers.add("header2");
        List<List<String>> rows = new ArrayList<>();
        List<String> row1 = new ArrayList<>();
        row1.add("1");
        row1.add("2");
        rows.add(row1);
        csvData.setHeaders(headers);
        csvData.setRows(rows);

        CSVData processedData = new CSVData();
        List<String> processedHeaders = new ArrayList<>();
        processedHeaders.add("header1");
        processedHeaders.add("header2");
        List<List<String>> processedRows = new ArrayList<>();
        List<String> processedRow1 = new ArrayList<>();
        processedRow1.add("1.0");
        processedRow1.add("2.0");
        processedRows.add(processedRow1);
        processedData.setHeaders(processedHeaders);
        processedData.setRows(processedRows);

        when(dataProcessingService.processData(csvData, operationRequest)).thenReturn(processedData);

        model.addAttribute("csvData", csvData);
        String result = dataProcessingController.processData(operationRequest, model);

        assertEquals("result", result);
        verify(mongoDBService, times(1)).saveData(processedData);
        verify(model, times(1)).addAttribute("processedData", processedData);
        verify(model, times(1)).addAttribute(eq("csvData"), eq(null));
    }

    @Test
    public void testProcessData_noCsvDataInSession() {
        OperationRequest operationRequest = new OperationRequest();
        String result = dataProcessingController.processData(operationRequest, model);
        assertEquals("redirect:/", result);
    }
}
