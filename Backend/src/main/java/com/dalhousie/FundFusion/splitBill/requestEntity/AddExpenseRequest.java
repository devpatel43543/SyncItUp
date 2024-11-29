package com.dalhousie.FundFusion.splitBill.requestEntity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddExpenseRequest {
    private Integer groupId;
    private String paidByEmail;
    private Double amount;
    private String title;
    private String category;
    private List<String> involvedMembers;  // Emails of the members involved

    /**
     * Validates the add expense request fields.
     *
     * @throws IllegalArgumentException if any field is invalid.
     */
    public void validate() {
        if (groupId == null || groupId < 0) {
            throw new IllegalArgumentException("Group ID must be a positive integer.");
        }
        if (isNullOrBlank(paidByEmail) || isInvalidEmailFormat(paidByEmail)) {
            throw new IllegalArgumentException("Invalid paid by email address.");
        }
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Amount must be a positive number.");
        }
        if (isNullOrBlank(title)) {
            throw new IllegalArgumentException("Title cannot be null or empty.");
        }
        if (isNullOrBlank(category)) {
            throw new IllegalArgumentException("Category cannot be null or empty.");
        }
        if (involvedMembers == null || involvedMembers.isEmpty()) {
            throw new IllegalArgumentException("Involved members list cannot be null or empty.");
        }
    }

    private boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean isInvalidEmailFormat(String email) {
        return !email.contains("@");
    }
}

