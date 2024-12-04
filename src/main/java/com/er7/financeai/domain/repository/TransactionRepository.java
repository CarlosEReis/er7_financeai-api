package com.er7.financeai.domain.repository;

import com.er7.financeai.domain.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserIdOrderByIdDesc(String userId);
}
