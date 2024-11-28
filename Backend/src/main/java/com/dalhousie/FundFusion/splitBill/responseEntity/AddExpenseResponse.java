package com.dalhousie.FundFusion.splitBill.responseEntity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddExpenseResponse {
    private Long transactionId;   // ID of the created transaction
    private String groupName;     // Name of the group
    private Double totalAmount;   // Total amount of the expense
    private String paidByEmail;   // Email of the user who paid
    private String title;
    private String category;// Description of the expense

    /**
     * Validates the response fields to ensure the data is correct.
     *
     * @throws IllegalArgumentException if any field is invalid.
     */
    public void validate() {
        if (transactionId == null || transactionId < 0) {
            throw new IllegalArgumentException("Transaction ID must be a valid positive number.");
        }
        if (isNullOrBlank(groupName)) {
            throw new IllegalArgumentException("Group name cannot be null or empty.");
        }
        if (totalAmount == null || totalAmount < 0) {
            throw new IllegalArgumentException("Total amount must be a positive number.");
        }
        if (isNullOrBlank(paidByEmail) || !isValidEmail(paidByEmail)) {
            throw new IllegalArgumentException("Paid by email must be a valid email address.");
        }
        if (isNullOrBlank(title)) {
            throw new IllegalArgumentException("Title cannot be null or empty.");
        }
        if (isNullOrBlank(category)) {
            throw new IllegalArgumentException("Category cannot be null or empty.");
        }
    }

    private boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean isValidEmail(String email) {
        return email.contains("@");
    }
}