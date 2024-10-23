package com.dalhousie.FundFusion.user.service;

import com.dalhousie.FundFusion.category.entity.Category;
import com.dalhousie.FundFusion.category.requestEntity.CategoryRequest;
import com.dalhousie.FundFusion.category.service.CategoryService;
import com.dalhousie.FundFusion.dto.DateRangeEntity;
import com.dalhousie.FundFusion.exception.UserTransactionNotFoundException;
import com.dalhousie.FundFusion.user.entity.User;
import com.dalhousie.FundFusion.user.entity.UserTransaction;
import com.dalhousie.FundFusion.user.repository.UserTransactionRepository;
import com.dalhousie.FundFusion.user.requestEntity.UserTransactionRequest;
import com.dalhousie.FundFusion.user.responseEntity.UserTransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserTransactionServiceImpl implements  UserTransactionService{

    private final UserTransactionRepository userTransactionRepository;
    private final CategoryService categoryService;
    private final UserService userService;

    @Override
    public UserTransactionResponse logTransaction(UserTransactionRequest request){

        User activeUer = userService.getCurrentUser();

        UserTransaction transaction = UserTransaction.builder()
                            .user(activeUer)
                            .expense(request.getExpense())
                            .category(
                                categoryService.getCategory(
                                    CategoryRequest.builder()
                                            .categoryId(request.getCategoryId())
                                            .build()
                                    )
                            )
                            .txnDesc(request.getTxnDesc())
                            .txnDate(request.getTxnDate())
                        .build();

        checkDate(transaction);

        UserTransaction savedTransaction = userTransactionRepository.save(transaction);

        return UserTransactionResponse.builder()
                .txnId(savedTransaction.getTxnId())
                .txnDesc(savedTransaction.getTxnDesc())
                .txnDate(savedTransaction.getTxnDate())
                .expense(savedTransaction.getExpense())
                .categoryId(savedTransaction.getCategory().getCategoryName())
                .build();

    }

    @Override
    public UserTransactionResponse updateTransaction(UserTransactionRequest request){


        UserTransaction existingTransaction =  userTransactionRepository.findById(request.getTxnId())
                .orElseThrow(
                        () -> new NoSuchElementException("INVALID_TRANSACTION ID: "+ request.getTxnId())
                );
        if(request.getExpense() != null)
            existingTransaction.setExpense(request.getExpense());
        if(request.getTxnDesc() != null)
            existingTransaction.setTxnDesc(request.getTxnDesc());
        if(request.getTxnDate() != null)
            existingTransaction.setTxnDate(request.getTxnDate());
        if(request.getCategoryId() != null)
            existingTransaction.setCategory(
                    categoryService.getCategory(
                        CategoryRequest.builder()
                                .categoryId(request.getCategoryId())
                                .build()
                    )
            );

        UserTransaction savedTransaction = userTransactionRepository.save(existingTransaction);

        return UserTransactionResponse.builder()
                .txnId(savedTransaction.getTxnId())
                .txnDesc(savedTransaction.getTxnDesc())
                .txnDate(savedTransaction.getTxnDate())
                .expense(savedTransaction.getExpense())
                .categoryId(savedTransaction.getCategory().getCategoryName())
                .build();
    }

    @Override
    public List<UserTransactionResponse> getAllTransactions() {

        User user = userService.getCurrentUser();
        List<UserTransaction> userTransactions = userTransactionRepository.findByUser(user);

        return userTransactions.stream().map(
                savedTransaction -> UserTransactionResponse.builder()
                        .txnId(savedTransaction.getTxnId())
                        .txnDesc(savedTransaction.getTxnDesc())
                        .txnDate(savedTransaction.getTxnDate())
                        .expense(savedTransaction.getExpense())
                        .categoryId(savedTransaction.getCategory().getCategoryName())
                        .build()
        ).collect(Collectors.toList());

    }

    @Override
    public List<UserTransactionResponse> getTransactionsWithinDateRange(DateRangeEntity dateRange) {
        LocalDate fromDate = dateRange.getFromDate();
        LocalDate toDate = dateRange.getToDate();
        try{
            if(fromDate.compareTo(toDate) > 0 )
                throw new Exception("fromDate is greater than toDate");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        User user = userService.getCurrentUser();
        List<UserTransaction> userTransactions = userTransactionRepository.findByUserAndTxnDateBetween(user,fromDate,toDate);

        return userTransactions.stream()
                .map(userTransaction -> UserTransactionResponse.builder()
                        .txnId(userTransaction.getTxnId())
                        .txnDesc(userTransaction.getTxnDesc())
                        .txnDate(userTransaction.getTxnDate())
                        .expense(userTransaction.getExpense())
                        .categoryId(userTransaction.getCategory().getCategoryName())
                        .build()
                ).collect(Collectors.toList());
    }

    @Override
    public List<UserTransactionResponse> getTransactionsWithCategory(UserTransactionRequest request) {

        User user = userService.getCurrentUser();
        Category category = categoryService.getCategory(
                CategoryRequest.builder()
                        .categoryId(request.getCategoryId())
                        .build()
        );
        List<UserTransaction> userTransactions = userTransactionRepository.findByUserAndCategory(user,category);

        return userTransactions.stream()
                .map(userTransaction -> UserTransactionResponse.builder()
                                .categoryId(userTransaction.getCategory().getCategoryName())
                                .txnId(userTransaction.getTxnId())
                                .txnDesc(userTransaction.getTxnDesc())
                                .expense(userTransaction.getExpense())
                                .txnDate(userTransaction.getTxnDate())
                                .build()
                        )
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTransaction(Integer txnId) {
        // Check if the transaction exists
        userTransactionRepository.findById(txnId)
                .orElseThrow(() -> new UserTransactionNotFoundException("INVALID_TRANSACTION ID: " + txnId));

        // Delete the transaction
        userTransactionRepository.deleteById(txnId);
    }

    public void checkDate(UserTransaction transaction){
        if(transaction.getTxnDate() == null)
            transaction.setTxnDate(LocalDate.now());
    }

}
