package com.dalhousie.FundFusion.user.service;

import com.dalhousie.FundFusion.category.entity.Category;
import com.dalhousie.FundFusion.category.repository.CategoryRepository;
import com.dalhousie.FundFusion.user.entity.UserTransaction;
import com.dalhousie.FundFusion.user.repository.UserTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserTransactionServiceImpl implements  UserTransactionService{

    private final UserTransactionRepository userTransactionRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public UserTransaction logTransaction(UserTransaction transaction){
        checkDate(transaction);

        Category fetchedCategory = categoryRepository.findById(transaction.getCategory().getCategoryId())
                        .orElseThrow(
                                () -> new NoSuchElementException("Invalid Category ID:"+ transaction.getCategory().getCategoryId())
                        );
        transaction.setCategory(fetchedCategory);
        return userTransactionRepository.save(transaction);
    }

    @Override
    public UserTransaction updateTransaction(UserTransaction transaction){
        UserTransaction existingTransaction =  userTransactionRepository.findById(transaction.getTxnId())
                .orElseThrow(
                        () -> new NoSuchElementException("INVALID_TRANSACTION ID: "+transaction.getTxnId())
                );
        if(transaction.getExpense() != null)
            existingTransaction.setExpense(transaction.getExpense());
        if(transaction.getTxnDesc() != null)
            existingTransaction.setTxnDesc(transaction.getTxnDesc());
        if(transaction.getTxnDate() != null)
            existingTransaction.setTxnDate(transaction.getTxnDate());
        if(transaction.getCategory() != null)
            existingTransaction.setCategory(transaction.getCategory());

        return userTransactionRepository.save(existingTransaction);
    }

    @Override
    public UserTransaction getTransactionWithTxnId(Integer txnId) {
        return userTransactionRepository.findById(txnId)
                .orElseThrow(
                        () -> new NoSuchElementException("NO TRANSACTION FOUND WITH ID: "+txnId)
                );
    }

    @Override
    public List<UserTransaction> getTransactionsWithinDateRange(LocalDate fromDate, LocalDate toDate) {
        try{
            if(fromDate.compareTo(toDate) > 0 )
                throw new Exception("fromDate is greater than toDate");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return userTransactionRepository.findByTxnDateBetween(fromDate,toDate);
    }

    @Override
    public List<UserTransaction> getTransactionsWithCategory(Category category) {
        return userTransactionRepository.findByCategory(category);
    }

    public void checkDate(UserTransaction transaction){
        if(transaction.getTxnDate() == null)
            transaction.setTxnDate(LocalDate.now());
    }

}
