package com.dalhousie.FundFusion.splitBill.responseEntity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class TransactionSummaryResponse {
    private Long transactionId;
    private Double amountPaid;
    private LocalDateTime transactionDate;
    private String paidByEmail;
    private Integer involvedMembersCount;
    private List<String> involvedMembers; // New field to include member emails
    private String title;
    private String category;

    /**
     * Validates the response fields.
     *
     * @throws IllegalArgumentException if any field is invalid.
     */
    public void validate() {
        if (transactionId == null || transactionId < 0) {
            throw new IllegalArgumentException("Transaction ID must be a valid positive number.");
        }
        if (amountPaid == null || amountPaid <= 0) {
            throw new IllegalArgumentException("Amount paid must be a positive number.");
        }
        if (isNullOrBlank(paidByEmail) || !isValidEmail(paidByEmail)) {
            throw new IllegalArgumentException("Paid by email must be a valid email address.");
        }
        if (involvedMembers == null || involvedMembersCount == null || involvedMembers.size() != involvedMembersCount) {
            throw new IllegalArgumentException("Involved members list and count must match.");
        }
        if (isNullOrBlank(title)) {
            throw new IllegalArgumentException("Title cannot be null or empty.");
        }
        if (isNullOrBlank(category)) {
            throw new IllegalArgumentException("Category cannot be null or empty.");
        }
    }

    /**
     * Provides a formatted string summarizing the transaction details.
     *
     * @return A summary string with the transaction details.
     */
    public String getTransactionSummary() {
        return String.format("Transaction [%s]: %.2f paid by %s on %s. Title: %s, Category: %s.",
                transactionId, amountPaid, paidByEmail, transactionDate, title, category);
    }

    private boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean isValidEmail(String email) {
        return email.contains("@");
    }
}
