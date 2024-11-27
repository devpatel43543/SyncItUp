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
}
