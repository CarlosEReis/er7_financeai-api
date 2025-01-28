package com.er7.financeai.domain.repository.projection;

import java.math.BigDecimal;

public interface TotalExpensePerCategory {

    String getCategoria();
    BigDecimal getSoma();

}
