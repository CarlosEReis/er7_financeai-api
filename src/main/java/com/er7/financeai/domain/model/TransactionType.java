package com.er7.financeai.domain.model;

public enum TransactionType {

    DEPOSIT("deposit"),
    EXPENSE("expense"),
    INVESTMENT("investment");

    private final String type;

    TransactionType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
