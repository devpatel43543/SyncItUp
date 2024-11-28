package com.dalhousie.FundFusion.splitBill.responseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDebtResponse {
    private String creditorEmail;
    private Integer groupId;
    private String groupName;
    private Double amount;

    /**
     * Validates the response fields.
     *
     * @throws IllegalArgumentException if any field is invalid.
     */
    public void validate() {
        if (isNullOrBlank(creditorEmail) || !isValidEmail(creditorEmail)) {
            throw new IllegalArgumentException("Creditor email must be a valid email address.");
        }
        if (groupId == null || groupId < 0) {
            throw new IllegalArgumentException("Group ID must be a valid positive number.");
        }
        if (isNullOrBlank(groupName)) {
            throw new IllegalArgumentException("Group name cannot be null or empty.");
        }
        if (amount == null || amount < 0) {
            throw new IllegalArgumentException("Amount must be a non-negative number.");
        }
    }

    private boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean isValidEmail(String email) {
        return email.contains("@");
    }
}
