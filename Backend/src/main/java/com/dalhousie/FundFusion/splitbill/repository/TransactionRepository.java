package com.dalhousie.FundFusion.splitBill.repository;


import com.dalhousie.FundFusion.group.entity.Group;
import com.dalhousie.FundFusion.splitBill.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByGroup(Group group);
}
