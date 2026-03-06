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
import com.er7.financeai.domain.repository.projection.TransactionListItem;
import com.er7.financeai.domain.service.TransactionService;
import com.er7.financeai.domain.service.UserService;
import org.springframework.http.HttpStatus;
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
    private final UserService userService;

    public TransactionController(TransactionService transactionService, TransactionRepository transactionRepository, UserService userService) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Helper para obter o ID Long do usuário logado a partir do userSub.
     * @param authentication O objeto de autenticação do Spring Security.
     * @return O ID Long do usuário.
     */
    private Long getAllowedUserId(Authentication authentication) {
        String userSub = authentication.getName();
        // Assumindo que você tem um método para obter o Long ID do User a partir do sub (String)
        return userService.findBySub(userSub).getId();
    }

    // =================================================================
    // 1. LEITURA (Refatorado anteriormente)
    // =================================================================

    /**
     * Busca todas as transações que o usuário logado tem permissão de leitura.
     */
    @GetMapping()
    public ResponseEntity<List<TransactionListItem>> listar(Authentication authentication) {
        Long allowedUserId = getAllowedUserId(authentication);

        // Chama o método que aplica a lógica de permissão (JOIN com SharingMember)
        List<TransactionListItem> visibleTransactions = transactionService.findAllTransactionsOnUserGroupMemberIsActive(allowedUserId);



        return ResponseEntity.ok(visibleTransactions);
    }

    // =================================================================
    // 2. ESCRITA/CRIAÇÃO/EDIÇÃO (Adicionando a proteção)
    // =================================================================

    /**
     * Salva ou atualiza uma transação. A proteção é feita dentro do TransactionService.
     * * Observação: Se for uma nova transação (POST), o campo 'transaction.user' deve
     * ser obrigatoriamente o usuário logado. Se for uma edição (PUT), o service
     * verifica a permissão sobre o 'transaction.user' existente.
     */
    @PostMapping
    public ResponseEntity<Transaction> saveTransaction(@RequestBody TransactionRequest transaction, Authentication authentication) {
        var owner = authentication.getName();

        // O Service verifica se allowedUserId tem permissão de escrita sobre o dono (transaction.getUser())
        Transaction savedTransaction = transactionService.saveTransaction(transaction.toDomainObject(), owner);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedTransaction);
    }

    // =================================================================
    // 3. EXCLUSÃO (Adicionando a proteção)
    // =================================================================

    /**
     * Deleta uma transação. A proteção de permissão de exclusão é feita dentro do TransactionService.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id, Authentication authentication) {
        Long allowedUserId = getAllowedUserId(authentication);

        // O Service busca a transação, identifica o dono e verifica a permissão de exclusão.
        transactionService.deleteTransaction(id, allowedUserId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/statistics/total-per-category")
    public ResponseEntity<List<EstatisticsReponse>> trasactionsPerCategory(Authentication authentication, @RequestParam("top") Integer top) {
        String userId = authentication.getName();
        User bySub = userService.findBySub(userId);


        List<TotalExpensePerCategory> transactionsPerCategory = transactionRepository.totalExpensePerCategory(bySub.getId(), top);

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
        User bySub = userService.findBySub(userId);
        EstatisticsBalanceResponse balance = transactionRepository.balanceObject(bySub.getId());

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
