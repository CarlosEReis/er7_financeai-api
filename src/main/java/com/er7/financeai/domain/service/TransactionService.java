package com.er7.financeai.domain.service;

import com.er7.financeai.domain.model.Transaction;
import com.er7.financeai.domain.model.User;
import com.er7.financeai.domain.repository.TransactionRepository;
import com.er7.financeai.domain.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionService {

    private final UserService userService;
    private final PaymentMethodService paymentMethodService;
    private final TransactionRepository transactionRepository;
    private final TransactionCategoryService transactionCategoryService;
    private final UserRepository userRepository;


    public TransactionService(
            UserService userService,
            TransactionRepository transactionRepository, PaymentMethodService paymentMethodService,
                              TransactionCategoryService transactionCategoryService, UserRepository userRepository) {
        this.userService = userService;
        this.transactionRepository = transactionRepository;
        this.paymentMethodService = paymentMethodService;
        this.transactionCategoryService = transactionCategoryService;
        this.userRepository = userRepository;
    }

    @Transactional
    public Transaction save(Transaction transaction, String sub) {
        var user = userService.findBySub(sub);
        if (user.getGroup() != null)
            transaction.setGroup(user.getGroup());
        transaction.setUser(user);
        var paymentMethod = paymentMethodService.findById(transaction.getPaymentMethod().getId());
        var category = transactionCategoryService.findById(transaction.getCategory().getId());

        transaction.setPaymentMethod(paymentMethod);
        transaction.setCategory(category);
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction update(Long id, Transaction transaction) {
        Transaction existingTransaction = findById(id);

//        if (!existingTransaction.getUserId().equals(transaction.getUserId()))
//            throw new RuntimeException("User ID mismatch");

        var paymentMethod = paymentMethodService.findById(transaction.getPaymentMethod().getId());
        var category = transactionCategoryService.findById(transaction.getCategory().getId());

        transaction.setPaymentMethod(paymentMethod);
        transaction.setCategory(category);

        // TODO: Verificar forma de mesmo que nao passar o createdAt e userId, manter os valores antigos
        BeanUtils.copyProperties(transaction, existingTransaction, "id", "createdAt", "userId", "deleted");

        return transactionRepository.save(existingTransaction);
    }

    public Transaction findById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
    }

    @Transactional
    public void delete(Long id, String userId) {
        Transaction transaction = findById(id);
//        if (!transaction.getUserId().equals(userId))
//            throw new RuntimeException("User ID mismatch");

        transactionRepository.delete(transaction);
    }

    public List<Transaction> findAllByUserSub(String userSub) {
        User user = userService.findBySub(userSub);
        if (user.getGroup() != null)
            return transactionRepository.findAllByGroupId(user.getGroup().getId());
        else
            return transactionRepository.findAllByUserSub(userSub);
    }

    void updateTransactionsToGroup(Long groupId, List<Long> userIds) {
        transactionRepository.updateTrasactionsByGroupIdAndUsers(groupId, userIds);
    }
}
