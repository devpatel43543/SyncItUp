package com.dalhousie.FundFusion.user.responseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTransactionResponse {

    private Integer txnId;
    private String txnDesc;
    private Float expense;
    private LocalDate txnDate;
    private String category;

}
