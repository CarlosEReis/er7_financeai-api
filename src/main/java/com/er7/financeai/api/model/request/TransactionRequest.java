package com.er7.financeai.api.model.request;

import com.er7.financeai.domain.model.PaymentMethod;
import com.er7.financeai.domain.model.Transaction;
import com.er7.financeai.domain.model.TransactionCategory;
import com.er7.financeai.domain.model.TransactionType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TransactionRequest (
        @NotNull TransactionType type,
        @NotBlank String name,
        @NotNull @Positive BigDecimal amount,
        @NotNull OffsetDateTime date,
        @NotNull CategoryRequest category,
        @NotNull PaymentMethodRequest paymentMethod
) {

    public Transaction toDomainObject() {
        Transaction transaction = new Transaction();
        transaction.setType(this.type);
        transaction.setName(this.name);
        transaction.setAmount(this.amount);
        transaction.setDate(this.date);

        TransactionCategory categoryEntity = new TransactionCategory();
        categoryEntity.setId(this.category.id());
        transaction.setCategory(categoryEntity);

        PaymentMethod paymentEntity = new PaymentMethod();
        paymentEntity.setId(this.paymentMethod.id());
        transaction.setPaymentMethod(paymentEntity);

        return transaction;
    }

    public record CategoryRequest(@NotNull Long id) {}
    public record PaymentMethodRequest(@NotNull Long id) {}
}
