package com.er7.financeai.api;

import com.er7.financeai.api.model.EstatisticsBalanceResponse;
import com.er7.financeai.api.model.EstatisticsReponse;
import com.er7.financeai.domain.model.Transaction;
import com.er7.financeai.domain.repository.TransactionRepository;
import com.er7.financeai.domain.repository.projection.TotalExpensePerCategory;
import com.er7.financeai.domain.repository.projection.TransactionBalance;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

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

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> buscar(@PathVariable Long id, Authentication authentication) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        return transaction.isPresent() ? ResponseEntity.ok(transaction.get()) : ResponseEntity.badRequest().build();
    }

    @GetMapping("/statistics/total-per-category")
    public ResponseEntity<List<EstatisticsReponse>> trasactionsPerCategory(Authentication authentication, @RequestParam("top") Integer top) {
        String userId = authentication.getName();
        List<TotalExpensePerCategory> transactionsPerCategory = transactionRepository.totalExpensePerCategory(userId, top);

        if (transactionsPerCategory.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        BigDecimal totalAllCategory = getTotalAllCategory(transactionsPerCategory);

        List<EstatisticsReponse> list = transactionsPerCategory.stream()
            .map(t -> {
                BigDecimal percentage = calcPercent(t.getSoma(), totalAllCategory);
                return new EstatisticsReponse(t.getCategoria(), t.getSoma(), percentage);
            })
            .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/statistics/balance")
    public ResponseEntity<EstatisticsBalanceResponse> transactionBalance(Authentication authentication) {
        String userId = authentication.getName();
        EstatisticsBalanceResponse balance = transactionRepository.balanceObject(userId);

        return ResponseEntity.ok(balance);
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id, Authentication authentication) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        if (transaction.isPresent()) {
            transactionRepository.delete(transaction.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    private BigDecimal getTotalAllCategory(List<TotalExpensePerCategory> transactions) {
        if (transactions.isEmpty())
            return BigDecimal.ZERO;
        return transactions.stream()
                .map(TotalExpensePerCategory::getSoma)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal calcPercent(BigDecimal valor, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.ZERO;
        return valor
                .divide(total, MathContext.DECIMAL64)
                .multiply(new BigDecimal(100))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
