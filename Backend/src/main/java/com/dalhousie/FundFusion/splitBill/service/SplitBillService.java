package com.dalhousie.FundFusion.splitBill.service;

import com.dalhousie.FundFusion.splitBill.requestEntity.AddExpenseRequest;
import com.dalhousie.FundFusion.splitBill.responseEntity.DebitCreditSummaryResponse;
import com.dalhousie.FundFusion.splitBill.requestEntity.SettleDebtRequest;
import com.dalhousie.FundFusion.splitBill.requestEntity.UpdateExpenseRequest;
import com.dalhousie.FundFusion.splitBill.responseEntity.*;

import java.util.List;

public interface SplitBillService {
        AddExpenseResponse createExpense(AddExpenseRequest request);
        AddExpenseResponse updateExpense(UpdateExpenseRequest request);
        void deleteExpense(Long transactionId);
        List<DebtResponse> getTransactionDebts(Integer groupId);
        List<DebtResponse> getgroupdebtsummary(Integer groupId);
        List<TransactionSummaryResponse> getTransactionSummary(Integer groupId);
        List<UserDebtResponse> getDebtsForAuthenticatedUserByGroup(Integer groupId);
        SettlementResponse settleDebt(SettleDebtRequest request);
        DebitCreditSummaryResponse getDebitCreditSummary(Integer groupId);
}
