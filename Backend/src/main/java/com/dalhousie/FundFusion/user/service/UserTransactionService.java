package com.dalhousie.FundFusion.user.service;

import com.dalhousie.FundFusion.category.entity.Category;
import com.dalhousie.FundFusion.user.entity.UserTransaction;

import java.time.LocalDate;
import java.util.List;

public interface UserTransactionService {

    UserTransaction logTransaction(UserTransaction userTransaction);

    UserTransaction updateTransaction(UserTransaction userTransaction);

    UserTransaction getTransactionWithTxnId(Integer txnId);

    List<UserTransaction> getTransactionsWithinDateRange(LocalDate fromDate, LocalDate toDate);

    List<UserTransaction> getTransactionsWithCategory(Category category);

}
