package com.er7.financeai.api.model;

import java.math.BigDecimal;

public record EstatisticsReponse (

        String category,
        BigDecimal total,
        BigDecimal percent)
{ }
