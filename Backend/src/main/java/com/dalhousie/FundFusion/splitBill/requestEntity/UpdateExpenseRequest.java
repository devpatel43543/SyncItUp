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
}
