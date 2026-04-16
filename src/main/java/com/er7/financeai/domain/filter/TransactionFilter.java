package com.er7.financeai.domain.filter;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;

public record TransactionFilter(
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    OffsetDateTime dateProcessStar,

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    OffsetDateTime dateProcessEnd,

    String searchTitle
) {}
