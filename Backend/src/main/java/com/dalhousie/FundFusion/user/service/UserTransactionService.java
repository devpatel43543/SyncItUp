package com.dalhousie.FundFusion.user.service;

import com.dalhousie.FundFusion.category.entity.Category;
import com.dalhousie.FundFusion.dto.DateRangeEntity;
import com.dalhousie.FundFusion.user.entity.UserTransaction;
import com.dalhousie.FundFusion.user.requestEntity.UserTransactionRequest;
import com.dalhousie.FundFusion.user.responseEntity.UserTransactionResponse;

import java.time.LocalDate;
import java.util.List;

public interface UserTransactionService {

    UserTransactionResponse logTransaction(UserTransactionRequest request);

    UserTransactionResponse updateTransaction(UserTransactionRequest request);

    List<UserTransactionResponse> getAllTransactions(UserTransactionRequest requests);

    List<UserTransactionResponse> getTransactionsWithinDateRange(DateRangeEntity dateRange);

    List<UserTransactionResponse> getTransactionsWithCategory(UserTransactionRequest request);

    void deleteTransaction(UserTransactionRequest request);

}
