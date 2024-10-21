package com.dalhousie.FundFusion.user.repository;

import com.dalhousie.FundFusion.category.entity.Category;
import com.dalhousie.FundFusion.user.entity.User;
import com.dalhousie.FundFusion.user.entity.UserTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserTransactionRepository extends JpaRepository<UserTransaction,Integer> {

    List<UserTransaction> findByUserAndTxnDateBetween(User user, LocalDate fromDate, LocalDate toDate);

    List<UserTransaction> findByUserAndCategory(User user, Category category);

    List<UserTransaction> findByUser(User user);

}
