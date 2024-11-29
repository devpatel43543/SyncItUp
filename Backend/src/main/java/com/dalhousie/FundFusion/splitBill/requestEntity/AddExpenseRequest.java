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
}

