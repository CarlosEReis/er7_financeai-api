package com.er7.financeai.api;

import com.er7.financeai.domain.model.Transaction;
import com.er7.financeai.domain.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/transactions")
public class TransactionController {

    private TransactionRepository transactionRepository;

    public TransactionController(TransactionRepository transactionRepository){
        this.transactionRepository = transactionRepository;
    }

    @GetMapping()
    public ResponseEntity<List<Transaction>> listar(Authentication authentication) {
        String userId = authentication.getName();
        List<Transaction> transactions = transactionRepository.findByUserIdOrderByIdDesc(userId);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping
    public ResponseEntity<Transaction> criar(@RequestBody Transaction transaction, Authentication authentication ) {
        transaction.setUserId(authentication.getName());
        Transaction savedTransaction = transactionRepository.save(transaction);
        return ResponseEntity.ok(savedTransaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> atualizar(@PathVariable Long id, @RequestBody Transaction transaction, Authentication authentication) {
        var transactionDB = transactionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
        BeanUtils.copyProperties(transaction, transactionDB, "id", "createdAt", "userId");
        transactionRepository.save(transactionDB);
        return ResponseEntity.ok(transactionDB);
    }
}
