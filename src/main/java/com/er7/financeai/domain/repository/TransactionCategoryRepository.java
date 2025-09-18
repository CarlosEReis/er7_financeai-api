package com.er7.financeai.domain.repository;

import com.er7.financeai.domain.model.TransactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionCategoryRepository extends JpaRepository<TransactionCategory, Long> {
}
