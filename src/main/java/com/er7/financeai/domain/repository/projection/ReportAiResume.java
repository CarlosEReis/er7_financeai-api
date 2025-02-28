package com.er7.financeai.domain.repository.projection;

import java.time.OffsetDateTime;

public interface ReportAiResume {

    Long getId();
    OffsetDateTime getCreatedAt();
    OffsetDateTime getReferentMonth();
}
