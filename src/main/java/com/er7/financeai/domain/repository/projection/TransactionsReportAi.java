package com.er7.financeai.domain.repository.projection;

import com.er7.financeai.domain.model.TransactionCategory;
import com.er7.financeai.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public interface TransactionsReportAi {

    OffsetDateTime getDate();
    BigDecimal getAmount();
    TransactionType getType();
    TransactionCategory getCategory();
}
