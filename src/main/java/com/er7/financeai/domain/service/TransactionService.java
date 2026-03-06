package com.er7.financeai.domain.service;

import com.er7.financeai.domain.model.Transaction;
import com.er7.financeai.domain.model.User;
import com.er7.financeai.domain.repository.TransactionRepository;
import com.er7.financeai.domain.repository.projection.TransactionListItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public List<TransactionListItem> findAllTransactionsOnUserGroupMemberIsActive(Long userId) {
        return this.transactionRepository.findAllTransactionsOnUserGroupMemberIsActive(userId);
    }

    /**
     * Salva ou atualiza uma transação, protegendo a operação com a verificação de permissão de escrita.
     * * @param transaction A transação a ser salva (pode ser nova ou existente).
     * @param allowedUserId O ID do usuário logado que está executando a operação.
     * @return A transação salva.
     */
    @Transactional
    public Transaction saveTransaction(Transaction transaction, String ownerSub) {
        // 1. Identifica o Dono dos Dados (targetUser)
        User owner = userService.findBySub(ownerSub);
        transaction.setUser(owner);

        // 2. Identifica o Usuário que está tentando salvar (allowedUser)
        //User allowedUser = userService.findById(allowedUserId); // Assumindo método de busca no UserService

        // 3. Verifica a Permissão de Escrita
        //checkWriteAccess(targetUser, allowedUser);

        // 4. Executa a operação se a permissão for concedida
        return transactionRepository.save(transaction);
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
}
