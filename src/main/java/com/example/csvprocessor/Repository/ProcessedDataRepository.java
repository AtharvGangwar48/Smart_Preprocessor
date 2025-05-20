package com.example.csvprocessor.repository;

import com.example.csvprocessor.model.ProcessedData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedDataRepository extends MongoRepository<ProcessedData, String> {
    // You can define custom query methods here if needed
}
