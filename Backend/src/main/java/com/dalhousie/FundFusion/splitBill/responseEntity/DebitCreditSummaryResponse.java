package com.dalhousie.FundFusion.splitBill.responseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DebitCreditSummaryResponse {
    private String userEmail;     // The email of the authenticated user
    private Double totalDebit;    // Total amount the user owes
    private Double totalCredit;   // Total amount the user is owed
    private Double totalGroupExpense; // Total expense for the group

    /**
     * Validates the response fields.
     *
     * @throws IllegalArgumentException if any field is invalid.
     */
    public void validate() {
        if (isNullOrBlank(userEmail) || !isValidEmail(userEmail)) {
            throw new IllegalArgumentException("Invalid user email.");
        }
        if (totalDebit == null || totalDebit < 0) {
            throw new IllegalArgumentException("Total debit must be a non-negative number.");
        }
        if (totalCredit == null || totalCredit < 0) {
            throw new IllegalArgumentException("Total credit must be a non-negative number.");
        }
        if (totalGroupExpense == null || totalGroupExpense < 0) {
            throw new IllegalArgumentException("Total group expense must be a non-negative number.");
        }
    }

    private boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean isValidEmail(String email) {
        return email.contains("@");
    }
}
