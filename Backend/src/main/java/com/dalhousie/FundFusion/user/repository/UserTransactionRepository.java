package com.dalhousie.FundFusion.user.repository;

import com.dalhousie.FundFusion.user.entity.UserTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTransactionRepository extends JpaRepository<UserTransaction,Integer> {
}
