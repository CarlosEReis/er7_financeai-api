package com.er7.financeai.api.model.response;

import com.er7.financeai.domain.model.Transaction;
import com.er7.financeai.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TransactionResponse(
    Long id,
    TransactionType type,
    String name,
    BigDecimal amount,
    Category category,
    OffsetDateTime date,
    PaymentMethod paymentMethod
) {
    public record Category(Long id, String name) {}
    public record PaymentMethod(Long id, String name) {}

    public static TransactionResponse toModelReponse(Transaction transaction) {
        return new TransactionResponse(
            transaction.getId(),
            transaction.getType(),
            transaction.getName(),
            transaction.getAmount(),
            transaction.getCategory() != null
                    ? new Category(transaction.getCategory().getId(), transaction.getCategory().getName())
                    : null,
            transaction.getDate(),
            transaction.getPaymentMethod() != null
                    ? new PaymentMethod(transaction.getPaymentMethod().getId(), transaction.getPaymentMethod().getName())
                    : null
        );
    }
}



