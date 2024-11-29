package com.dalhousie.FundFusion.user.requestEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTransactionRequest {

    private Integer txnId;
    private String txnDesc;
    private Float expense;
    private LocalDate txnDate;
    private Integer categoryId;

    /**
     * Validates the transaction request fields to ensure data correctness.
     *
     * @throws IllegalArgumentException if any field is invalid.
     */
    public void validate() {
        if (txnId == null || txnId < 0) {
            throw new IllegalArgumentException("Transaction ID must be a valid positive number.");
        }
        if (isNullOrBlank(txnDesc)) {
            throw new IllegalArgumentException("Transaction description cannot be null or empty.");
        }
        if (expense == null || expense <= 0) {
            throw new IllegalArgumentException("Expense must be a positive number.");
        }
        if (txnDate == null) {
            throw new IllegalArgumentException("Transaction date must be specified.");
        }
        if (categoryId == null || categoryId < 0) {
            throw new IllegalArgumentException("Category ID must be a valid positive number.");
        }
    }

    private boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }
}

