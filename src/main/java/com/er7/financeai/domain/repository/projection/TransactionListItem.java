package com.er7.financeai.domain.repository.projection;

import java.math.BigDecimal;
import java.time.Instant;

public interface TransactionListItem {
    Long getId();
    String getName();

    String getRegistradoPor();
    String getPicture();

    String getPaymentMethodName();
    String getType();

    String getCategoria();
    String getGrupo();

    BigDecimal getAmount();
    Instant getCreatedAt();
    Instant getDateProcess();
    String getStatusPayment();
}