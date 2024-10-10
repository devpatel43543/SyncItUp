package com.dalhousie.FundFusion.user.repository;

import com.dalhousie.FundFusion.category.entity.Category;
import com.dalhousie.FundFusion.user.entity.UserTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserTransactionRepository extends JpaRepository<UserTransaction,Integer> {

    public List<UserTransaction> findByTxnDateBetween(LocalDate fromDate, LocalDate toDate);

    public List<UserTransaction> findByCategory(Category category);

}
