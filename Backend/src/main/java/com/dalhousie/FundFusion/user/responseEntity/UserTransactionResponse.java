package com.dalhousie.FundFusion.user.responseEntity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserTransactionResponse {

    private String txnDesc;
    private Float expense;
    private LocalDate txnDate;
    private Integer categoryId;

}
