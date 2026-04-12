package com.er7.financeai.api.model.request;

import com.er7.financeai.domain.model.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TransactionRequest (
        @NotNull TransactionType type,
        @NotBlank String name,
        @NotNull @Positive BigDecimal amount,
        @NotNull OffsetDateTime date,
        @NotNull CategoryRequest category,
        @NotNull PaymentMethodRequest paymentMethod,
        @NotNull GroupRequest group,
        @NotNull PaymentType paymentType,
        @NotNull Integer numberParcels,
        @NotNull StatusPayment statusPayment
) {

        public Transaction toDomainObject() {
        Transaction transaction = new Transaction();
        transaction.setType(this.type);
        transaction.setName(this.name);
        transaction.setAmount(this.amount);
        transaction.setDate(this.date);
        transaction.setStatusPayment(this.statusPayment);

        TransactionCategory categoryEntity = new TransactionCategory();
        categoryEntity.setId(this.category.id());
        transaction.setCategory(categoryEntity);

        PaymentMethod paymentEntity = new PaymentMethod();
        paymentEntity.setId(this.paymentMethod.id());
        transaction.setPaymentMethod(paymentEntity);

        Group group = new Group();
        group.setId(this.group.id);
        transaction.setGroup(group);

        transaction.setPaymentType(this.paymentType);
        transaction.setNumberInstallments(this.numberParcels);

        return transaction;
    }

    public record CategoryRequest(@NotNull Long id) {}
    public record PaymentMethodRequest(@NotNull Long id) {}
    public record GroupRequest(@NotNull Integer id) {}
}
