package com.dalhousie.FundFusion.splitBill.requestEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettleDebtRequest {
    private Integer groupId;
    private String creditorEmail;
    private Double amount;

    /**
     * Validates the settle debt request fields.
     *
     * @throws IllegalArgumentException if any field is invalid.
     */
    public void validate() {
        if (groupId == null || groupId < 0) {
            throw new IllegalArgumentException("Group ID must be a positive integer.");
        }
        if (isNullOrBlank(creditorEmail) || isInvalidEmailFormat(creditorEmail)) {
            throw new IllegalArgumentException("Invalid creditor email address.");
        }
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Amount must be a positive number.");
        }
    }

    private boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean isInvalidEmailFormat(String email) {
        return !email.contains("@");
    }
}
