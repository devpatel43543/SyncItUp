package com.dalhousie.FundFusion.splitBill.responseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementResponse {
    private String debtorEmail;
    private String creditorEmail;
    private Double amountSettled;
    private String message;
    /**
     * Validates the response fields.
     *
     * @throws IllegalArgumentException if any field is invalid.
     */
    public void validate() {
        if (isNullOrBlank(debtorEmail) || !isValidEmail(debtorEmail)) {
            throw new IllegalArgumentException("Debtor email must be a valid email address.");
        }
        if (isNullOrBlank(creditorEmail) || !isValidEmail(creditorEmail)) {
            throw new IllegalArgumentException("Creditor email must be a valid email address.");
        }
        if (amountSettled == null || amountSettled <= 0) {
            throw new IllegalArgumentException("Amount settled must be a positive number.");
        }
    }

    private boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean isValidEmail(String email) {
        return email.contains("@");
    }
}
