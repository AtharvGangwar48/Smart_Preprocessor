package com.example.csvprocessor.controller;

import com.example.csvprocessor.exception.FileUploadException;
import com.example.csvprocessor.model.CSVData;
import com.example.csvprocessor.model.HeaderColumn;
import com.example.csvprocessor.model.OperationRequest;
import com.example.csvprocessor.service.DataProcessingService;
import com.example.csvprocessor.service.MongoDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@SessionAttributes("csvData")
public class DataProcessingController {

    private final DataProcessingService dataProcessingService;
    private final MongoDBService mongoDBService;

    @Autowired
    public DataProcessingController(DataProcessingService dataProcessingService, MongoDBService mongoDBService) {
        this.dataProcessingService = dataProcessingService;
        this.mongoDBService = mongoDBService;
    }

    @GetMapping("/")
    public String index() {
        return "upload";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("message", "Please select a file to upload");
            return "upload";
        }

        try {
            CSVData csvData = dataProcessingService.parseCSV(file);
            List<HeaderColumn> headerColumns = csvData.getHeaders().stream()
                    .map(HeaderColumn::new)
                    .collect(Collectors.toList());

            model.addAttribute("csvData", csvData);
            model.addAttribute("headerColumns", headerColumns);
            model.addAttribute("operationRequest", new OperationRequest());
            return "process";
        } catch (IOException e) {
            throw new FileUploadException("Error processing file", e);
        }
    }

    @PostMapping("/process")
    public String processData(OperationRequest operationRequest, @SessionAttribute("csvData") CSVData csvData, Model model, SessionStatus sessionStatus) {
        if (csvData == null) {
            return "redirect:/";
        }

        CSVData processedData = dataProcessingService.processData(csvData, operationRequest);

        // Store data in MongoDB in Chart.js compatible format
        mongoDBService.saveDataForCharts(processedData, "CSV Data Chart"); // You can make the chart title dynamic

        model.addAttribute("processedData", processedData); // You might still want to display processed data
        sessionStatus.setComplete();
        return "result";
    }
}