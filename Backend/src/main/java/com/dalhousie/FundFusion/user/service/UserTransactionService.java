package com.dalhousie.FundFusion.user.service;

import com.dalhousie.FundFusion.category.entity.Category;
import com.dalhousie.FundFusion.user.entity.UserTransaction;
import com.dalhousie.FundFusion.user.requestEntity.UserTransactionRequest;
import com.dalhousie.FundFusion.user.responseEntity.UserTransactionResponse;

import java.time.LocalDate;
import java.util.List;

public interface UserTransactionService {

    UserTransactionResponse logTransaction(UserTransactionRequest userTransaction);

    UserTransaction updateTransaction(UserTransaction userTransaction);

    UserTransaction getTransactionWithTxnId(Integer txnId);

    List<UserTransaction> getTransactionsWithinDateRange(LocalDate fromDate, LocalDate toDate);

    List<UserTransaction> getTransactionsWithCategory(Category category);

}
