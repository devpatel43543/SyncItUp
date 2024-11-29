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
}