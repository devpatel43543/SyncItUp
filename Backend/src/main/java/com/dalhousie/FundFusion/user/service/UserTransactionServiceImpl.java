package com.dalhousie.FundFusion.user.service;

import com.dalhousie.FundFusion.category.entity.Category;
import com.dalhousie.FundFusion.category.service.CategoryService;
import com.dalhousie.FundFusion.user.entity.UserTransaction;
import com.dalhousie.FundFusion.user.repository.UserTransactionRepository;
import com.dalhousie.FundFusion.user.requestEntity.UserTransactionRequest;
import com.dalhousie.FundFusion.user.responseEntity.UserTransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserTransactionServiceImpl implements  UserTransactionService{

    private final UserTransactionRepository userTransactionRepository;
    private final CategoryService categoryService;
    private final UserService userService;

    @Override
    public UserTransactionResponse logTransaction(UserTransactionRequest request){

        UserTransaction transaction = UserTransaction.builder()
                            .user(userService.getUser(request.getUserId()))
                            .expense(request.getExpense())
                            .category(categoryService.getCategory(request.getCategoryId()))
                            .txnDesc(request.getTxnDesc())
                            .txnDate(request.getTxnDate())
                        .build();

        checkDate(transaction);

        UserTransaction savedTransaction = userTransactionRepository.save(transaction);

        return UserTransactionResponse.builder()
                .txnDesc(savedTransaction.getTxnDesc())
                .txnDate(savedTransaction.getTxnDate())
                .expense(savedTransaction.getExpense())
                .categoryId(savedTransaction.getCategory().getCategoryId())
                .build();

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
