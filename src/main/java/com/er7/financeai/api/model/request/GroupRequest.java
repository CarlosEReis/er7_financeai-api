package com.er7.financeai.api.model.request;

import jakarta.validation.constraints.NotEmpty;

public record GroupRequest (
        @NotEmpty String name,
        @NotEmpty String description
) {}
