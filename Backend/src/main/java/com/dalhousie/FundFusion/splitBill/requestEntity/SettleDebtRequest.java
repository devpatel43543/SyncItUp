package com.dalhousie.FundFusion.splitBill.requestEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettleDebtRequest {
    private Integer groupId;
    private String creditorEmail;
    private Double amount;
}
