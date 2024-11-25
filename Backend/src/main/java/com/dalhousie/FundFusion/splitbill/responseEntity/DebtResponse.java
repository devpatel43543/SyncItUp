package com.dalhousie.FundFusion.splitBill.responseEntity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DebtResponse {
    private String paidByEmail;  // The user who paid
    private String owesToEmail; // The user who owes
    private Double amount;        // Amount owed
}
