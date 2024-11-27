package com.dalhousie.FundFusion.splitBill.repository;


import com.dalhousie.FundFusion.group.entity.Group;
import com.dalhousie.FundFusion.splitBill.entity.ExpenseShare;
import com.dalhousie.FundFusion.splitBill.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseShareRepository extends JpaRepository<ExpenseShare, Long> {
    List<ExpenseShare> findByTransaction(Transaction transaction);
    List<ExpenseShare> findByTransactionGroup(Group group);

}