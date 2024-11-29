package com.dalhousie.FundFusion.user.responseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTransactionResponse {

    private Integer txnId;
    private String txnDesc;
    private Float expense;
    private LocalDate txnDate;
    private String category;

    /**
     * Validates the transaction response fields to ensure data correctness.
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
        if (isNullOrBlank(category)) {
            throw new IllegalArgumentException("Category cannot be null or empty.");
        }
    }

    private boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }
}
