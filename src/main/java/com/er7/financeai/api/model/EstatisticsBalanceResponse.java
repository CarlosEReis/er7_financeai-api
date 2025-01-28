package com.er7.financeai.api.model;

import java.math.BigDecimal;

public record EstatisticsBalanceResponse(
    BigDecimal deposit,
    BigDecimal expense,
    BigDecimal investment,
    BigDecimal balance
) { }
