package com.dalhousie.FundFusion.user.service;

import com.dalhousie.FundFusion.dto.DateRangeEntity;
import com.dalhousie.FundFusion.user.requestEntity.UserTransactionRequest;
import com.dalhousie.FundFusion.user.responseEntity.UserTransactionResponse;

import java.util.List;

public interface UserTransactionService {

    UserTransactionResponse logTransaction(UserTransactionRequest request);

    UserTransactionResponse updateTransaction(UserTransactionRequest request);

    List<UserTransactionResponse> getAllTransactions();

    List<UserTransactionResponse> getTransactionsWithinDateRange(DateRangeEntity dateRange);

    List<UserTransactionResponse> getTransactionsWithCategory(UserTransactionRequest request);

    void deleteTransaction(Integer id);

}
