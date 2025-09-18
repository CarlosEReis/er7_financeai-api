package com.er7.financeai.common;

import com.er7.financeai.api.model.request.TransactionRequest;
import com.er7.financeai.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

public class TransactionConstants {

    public static final String BASE_URL = "/v1/transactions";

    public static final TransactionRequest TRANSACTION_REQUEST_VALID = new TransactionRequest(
        TransactionType.EXPENSE,
        "Conta de Luz",
        BigDecimal.valueOf(170.50),
        OffsetDateTime.of(LocalDate.now(), LocalTime.now(), java.time.ZoneOffset.UTC),
        new TransactionRequest.CategoryRequest(1L),
        new TransactionRequest.PaymentMethodRequest(1L));

    public static final TransactionRequest TRANSACTION_REQUEST_UPDATE_VALID = new TransactionRequest(
            TransactionType.DEPOSIT,
            "Conta de Água",
            BigDecimal.valueOf(270.50),
            OffsetDateTime.of(LocalDate.now(), LocalTime.now(), java.time.ZoneOffset.UTC),
            new TransactionRequest.CategoryRequest(2L),
            new TransactionRequest.PaymentMethodRequest(2L));

}
