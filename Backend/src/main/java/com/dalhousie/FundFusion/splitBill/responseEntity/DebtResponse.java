package com.dalhousie.FundFusion.splitBill.responseEntity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DebtResponse {
    private String paidByEmail;  // The user who paid
    private String owesToEmail; // The user who owes
    private Double amount;        // Amount owed

    /**
     * Validates the response fields.
     *
     * @throws IllegalArgumentException if any field is invalid.
     */
    public void validate() {
        if (isNullOrBlank(paidByEmail) || !isValidEmail(paidByEmail)) {
            throw new IllegalArgumentException("Paid by email must be a valid email address.");
        }
        if (isNullOrBlank(owesToEmail) || !isValidEmail(owesToEmail)) {
            throw new IllegalArgumentException("Owes to email must be a valid email address.");
        }
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Amount must be a positive number.");
        }
    }

    /**
     * Provides a summary of the debt details.
     *
     * @return A human-readable string representation of the debt information.
     */
    public String getDebtSummary() {
        return String.format("%s owes %s an amount of %.2f.", owesToEmail, paidByEmail, amount);
    }

    private boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean isValidEmail(String email) {
        return email.contains("@");
    }
}
