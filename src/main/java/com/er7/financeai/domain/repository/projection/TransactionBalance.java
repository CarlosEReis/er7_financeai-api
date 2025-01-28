package com.er7.financeai.domain.repository.projection;

import java.math.BigDecimal;

public interface TransactionBalance {
    BigDecimal getType();
    BigDecimal getTotal();
}