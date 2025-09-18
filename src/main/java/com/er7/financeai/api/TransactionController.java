package com.er7.financeai.api;

import com.er7.financeai.api.model.EstatisticsBalanceResponse;
import com.er7.financeai.api.model.EstatisticsReponse;
import com.er7.financeai.api.model.request.TransactionRequest;
import com.er7.financeai.api.model.response.TransactionResponse;
import com.er7.financeai.domain.model.Transaction;
import com.er7.financeai.domain.model.User;
import com.er7.financeai.domain.repository.TransactionRepository;
import com.er7.financeai.domain.repository.UserRepository;
import com.er7.financeai.domain.repository.projection.TotalExpensePerCategory;
import com.er7.financeai.domain.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionController(TransactionService transactionService, TransactionRepository transactionRepository, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.transactionService = transactionService;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping()
    public ResponseEntity<List<Transaction>> listar(Authentication authentication) {
        String userSub = authentication.getName();
        //List<Transaction> transactions = transactionRepository.findByUserIdOrderByIdDesc(userSub);
        //List<Transaction> transactions = transactionRepository.findByUserSub(userSub);
        List<Transaction> transactions = transactionService.findAllByUserSub(userSub);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> buscar(@PathVariable Long id, Authentication authentication) {
        var transactionResponse = TransactionResponse.toModelReponse(transactionService.findById(id));
        return ResponseEntity.ok(transactionResponse);
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> criar(@RequestBody @Valid TransactionRequest transactionRequest, @AuthenticationPrincipal Jwt principal, Authentication authentication) {
        Transaction transaction = transactionRequest.toDomainObject();
        Transaction savedTransaction = transactionService.save(transaction, authentication.getName());
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
            .path("/{id}")
            .buildAndExpand(savedTransaction.getId())
            .toUri();
        return ResponseEntity.created(uri).body(TransactionResponse.toModelReponse(savedTransaction));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> atualizar(@PathVariable Long id, @RequestBody TransactionRequest transactionRequest, Authentication authentication) {
        var transaction = transactionRequest.toDomainObject();
        //transaction.setUserId(authentication.getName());
        Transaction transactionUpdated = transactionService.update(id, transaction);
        return ResponseEntity.ok(TransactionResponse.toModelReponse(transactionUpdated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id, Authentication authentication) {
        transactionService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/statistics/total-per-category")
    public ResponseEntity<List<EstatisticsReponse>> trasactionsPerCategory(Authentication authentication, @RequestParam("top") Integer top) {
        String userId = authentication.getName();
        Optional<User> bySub = userRepository.findBySub(userId);


        List<TotalExpensePerCategory> transactionsPerCategory = transactionRepository.totalExpensePerCategory(bySub.get().getId(), top);

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
        Optional<User> bySub = userRepository.findBySub(userId);
        EstatisticsBalanceResponse balance = transactionRepository.balanceObject(bySub.get().getId());

        return ResponseEntity.ok(balance);
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
