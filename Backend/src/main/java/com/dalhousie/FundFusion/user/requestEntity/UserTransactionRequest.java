package com.dalhousie.FundFusion.user.requestEntity;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTransactionRequest {

    private Integer userId;
    private Integer txnId;
    private String txnDesc;
    @NotNull(message = "Expense cannot be empty")
    private Float expense;
    private LocalDate txnDate;
    private Integer categoryId;

}

