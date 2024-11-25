package com.dalhousie.FundFusion.splitBill.responseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDebtResponse {
    private String creditorEmail;
    private Integer groupId;
    private String groupName;
    private Double amount;
}
