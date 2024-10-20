package com.dalhousie.FundFusion.user.requestEntity;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserTransactionRequest {

    private Integer userId;
    private String txnDesc;

    @NotNull(message = "Expense cannot be empty")
    private Float expense;
    private LocalDate txnDate;
    private Integer categoryId;

}

