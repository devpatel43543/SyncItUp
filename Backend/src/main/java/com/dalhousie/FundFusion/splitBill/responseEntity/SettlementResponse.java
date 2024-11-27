package com.dalhousie.FundFusion.splitBill.responseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementResponse {
    private String debtorEmail;
    private String creditorEmail;
    private Double amountSettled;
    private String message;
}
