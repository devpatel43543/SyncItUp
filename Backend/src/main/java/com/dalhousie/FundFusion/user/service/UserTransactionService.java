package com.dalhousie.FundFusion.user.service;

import com.dalhousie.FundFusion.user.entity.UserTransaction;
import com.dalhousie.FundFusion.user.repository.UserTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserTransactionService {

    private final UserTransactionRepository userTransactionRepository;

    public void logTransaction(UserTransaction transaction){
        transaction.setTxnDate(LocalDate.now());
        userTransactionRepository.save(transaction);
    }

}
