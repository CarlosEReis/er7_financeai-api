package com.er7.financeai.domain.service;

import com.er7.financeai.domain.model.TransactionCategory;
import com.er7.financeai.domain.repository.TransactionCategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class TransactionCategoryService {

    private final TransactionCategoryRepository transactionCategoryRepository;

    public TransactionCategoryService(TransactionCategoryRepository transactionCategoryRepository) {
        this.transactionCategoryRepository = transactionCategoryRepository;
    }

    public TransactionCategory findById(Long id) {
        return transactionCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction category not found"));
    }
}
