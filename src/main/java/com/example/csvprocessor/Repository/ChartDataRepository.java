package com.example.csvprocessor.repository;

import com.example.csvprocessor.model.ChartData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChartDataRepository extends MongoRepository<ChartData, String> {
    // You can add custom query methods if needed, e.g., find by chartTitle
}