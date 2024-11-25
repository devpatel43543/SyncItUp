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

}
