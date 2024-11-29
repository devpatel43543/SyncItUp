package com.dalhousie.FundFusion.splitBill.requestEntity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateExpenseRequest {
    @NotNull(message = "Transaction ID is required.")
    private Long transactionId; // ID of the transaction to update

    @NotNull(message = "Amount is required.")
    private Double amount; // Updated amount of the expense

    @NotBlank(message = "Description is required.")
    private String title; // Updated description of the expense

    @NotBlank(message = "category is required.")
    private String category;

    @NotEmpty(message = "Involved members cannot be empty.")
    private List<String> involvedMembers; // Updated list of involved members' emails


    /**
     * Validates the update expense request fields.
     *
     * @throws IllegalArgumentException if any field is invalid.
     */
    public void validate() {
        if (transactionId == null || transactionId < 0) {
            throw new IllegalArgumentException("Transaction ID must be a positive number.");
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
}
