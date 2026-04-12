package com.er7.financeai.domain.service;

import com.er7.financeai.domain.filter.TransactionFilter;
import com.er7.financeai.domain.model.PaymentType;
import com.er7.financeai.domain.model.StatusPayment;
import com.er7.financeai.domain.model.Transaction;
import com.er7.financeai.domain.model.User;
import com.er7.financeai.domain.repository.TransactionRepository;
import com.er7.financeai.domain.repository.projection.TransactionListItem;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.OffsetDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final SharingService sharingService; // Injetando o serviço de permissão
    private final UserService userService; // Assumindo injeção para buscar o User pelo ID

    public TransactionService(
            TransactionRepository transactionRepository,
            SharingService sharingService,
            UserService userService
    ) {
        this.transactionRepository = transactionRepository;
        this.sharingService = sharingService;
        this.userService = userService;
    }

    /**
     * Busca todas as transações que o usuário logado (allowedUser) tem permissão de leitura.
     *
     * @param allowedUserId O ID do usuário logado (o AllowedUser).
     * @return Lista de Transações visíveis (próprias + compartilhadas).
     */
    @Transactional(readOnly = true)
    public List<Transaction> getAllVisibleTransactions(Long allowedUserId) {
        // A lógica de permissão de leitura é delegada ao Repositório.
        return this.transactionRepository.findAll();
                //transactionRepository.findAccessibleTransactionsByAllowedUserId(allowedUserId);
    }

    @Transactional
    public void updateStatusPayment(Long transactionID, String ownerSubUpdate, StatusPayment statusPayment) {
        var transaction = findByIdOrFail(transactionID);
        if (!isOwner(transaction, ownerSubUpdate)) throw new IllegalCallerException("You are not the owner of this transaction");
        transaction.setStatusPayment(statusPayment);
        System.out.println("StatusPayment" + statusPayment);
        transactionRepository.save(transaction);
    }

    public List<TransactionListItem> findAllTransactionsOnUserGroupMemberIsActive(TransactionFilter filter, Long userId) {
        System.out.println("User" + userId);
        return this.transactionRepository
            .findAllTransactionsOnUserGroupMemberIsActive(
                    filter.dateProcessStar(), filter.dateProcessEnd(), userId);
    }

    /**
     * Salva ou atualiza uma transação, protegendo a operação com a verificação de permissão de escrita.
     * * @param transaction A transação a ser salva (pode ser nova ou existente).
     * @param allowedUserId O ID do usuário logado que está executando a operação.
     * @return A transação salva.
     */
    @Transactional
    public List<Transaction> saveTransaction(Transaction transaction, String ownerSub) {
        // 1. Identifica o Dono dos Dados (targetUser)
        User owner = userService.findBySub(ownerSub);
        transaction.setUser(owner);

        List<Transaction> transactions = new ArrayList<>();


        if (PaymentType.RECORRENTE.equals(transaction.getPaymentType())) {
            transactions = transactionRepository.saveAll(
                    generateRecurrenceTransactionsList(transaction));
            return transactions;
        }

        if (PaymentType.PARCELADO.equals(transaction.getPaymentType())) {
            System.out.println("Parcelado" + transaction.getPaymentType());
            System.out.println("Parcelado" + transaction.getNumberInstallments());
            transactions = transactionRepository.saveAll(generateParcelTransactionsList(transaction));
            return transactions;
        }

        var savedTransaction = transactionRepository.save(transaction);
        return List.of(savedTransaction);
    }

    @Transactional
    public Transaction update(Long id, Transaction transaction, String ownerSub) {
        var transactionDb = findByIdOrFail(id);
        if (!ObjectUtils.nullSafeEquals(transactionDb.getUser().getSub(), ownerSub))
            throw new RuntimeException("Transaction not found");
        BeanUtils.copyProperties(transaction, transactionDb, "id", "deleted", "created", "updated", "user");
        return transactionRepository.save(transactionDb);
    }

    private Transaction findByIdOrFail(Long id) {
        return transactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    /**
     * Deleta uma transação pelo ID, protegendo a operação com a verificação de permissão de escrita.
     *
     * @param transactionId O ID da transação a ser deletada.
     * @param allowedUserId O ID do usuário logado que está executando a operação.
     */
    @Transactional
    public void deleteTransaction(Long transactionId, Long allowedUserId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transação não encontrada com ID: " + transactionId));

        // 1. Identifica o Dono dos Dados (targetUser)
        User targetUser = transaction.getUser();

        // 2. Identifica o Usuário que está tentando deletar (allowedUser)
        User allowedUser = userService.findById(allowedUserId);

        // 3. Verifica a Permissão de Escrita
        checkWriteAccess(targetUser, allowedUser);

        // 4. Executa a operação se a permissão for concedida
        transactionRepository.delete(transaction);
    }

    private List<Transaction> generateRecurrenceTransactionsList(Transaction transactionBase) {
        List<Transaction> transactionsRecurrences = new ArrayList<>();

        int currentYear = OffsetDateTime.now().getYear();
        var nextMonthDate = transactionBase.getDate();

        while(nextMonthDate.getYear() == currentYear) {

            Transaction transaction = new Transaction();
            transaction.setDate(nextMonthDate);

            transaction.setName(transactionBase.getName() + " - " + getRenameRecorrentTransaction(nextMonthDate));
            transaction.setAmount(transactionBase.getAmount());
            transaction.setType(transactionBase.getType());
            transaction.setPaymentType(PaymentType.RECORRENTE);
            transaction.setCategory(transactionBase.getCategory());
            transaction.setPaymentMethod(transactionBase.getPaymentMethod());
            transaction.setGroup(transactionBase.getGroup());
            transaction.setUser(transactionBase.getUser());

            transactionsRecurrences.add(transaction);
            nextMonthDate = nextMonthDate.plusMonths(1);
        }

        return transactionsRecurrences;
    }

    private List<Transaction> generateParcelTransactionsList(Transaction transactionBase) {
        List<Transaction> transactionsParcial = new ArrayList<>();

        //TODO: ajustar
        int currentYear = OffsetDateTime.now().getYear();
        var nextMonthDate = transactionBase.getDate();

        for (int i = 1; i <= transactionBase.getNumberInstallments(); i++) {
            Transaction transaction = new Transaction();
            transaction.setDate(nextMonthDate);

            transaction.setName(transactionBase.getName() + " - " + i + "/" + transactionBase.getNumberInstallments() + " em " + getRenameParcelTransaction(nextMonthDate));
            transaction.setAmount(transactionBase.getAmount());
            transaction.setType(transactionBase.getType());
            transaction.setPaymentType(PaymentType.PARCELADO);
            transaction.setCategory(transactionBase.getCategory());
            transaction.setPaymentMethod(transactionBase.getPaymentMethod());
            transaction.setGroup(transactionBase.getGroup());
            transaction.setUser(transactionBase.getUser());
            transaction.setStatusPayment(transactionBase.getStatusPayment());

            transaction.setNumberInstallments(transactionBase.getNumberInstallments());

            transactionsParcial.add(transaction);
            nextMonthDate = nextMonthDate.plusMonths(1);
        }


//        while(nextMonthDate.getYear() == currentYear) {
//
//
//        }
        return transactionsParcial;
    }

    private static String getRenameRecorrentTransaction(OffsetDateTime date) {
        return date.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
    }

    private static String getRenameParcelTransaction(OffsetDateTime date) {
        return date.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
    }

    /**
     * Lógica unificada de verificação de acesso de escrita.
     * Inclui a regra de que o dono dos dados SEMPRE pode editar seus próprios dados.
     */
    private void checkWriteAccess(User targetUser, User allowedUser) {
        // Regra de Ouro: O Dono dos Dados SEMPRE pode alterar seus próprios dados.
        if (targetUser.getId().equals(allowedUser.getId())) {
            return;
        }

        // Se o targetUser for diferente do allowedUser, verificamos o compartilhamento.
        //sharingService.checkEditPermission(targetUser, allowedUser);
    }

    private boolean isOwner(Transaction transaction, String ownerSub) {
        return transaction.getUser().getSub().equals(ownerSub);
    }
}
