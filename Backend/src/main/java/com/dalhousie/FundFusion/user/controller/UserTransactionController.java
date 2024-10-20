package com.dalhousie.FundFusion.user.controller;

import com.dalhousie.FundFusion.category.entity.Category;
import com.dalhousie.FundFusion.dto.DateRangeEntity;
import com.dalhousie.FundFusion.user.entity.UserTransaction;
import com.dalhousie.FundFusion.user.requestEntity.UserTransactionRequest;
import com.dalhousie.FundFusion.user.responseEntity.UserTransactionResponse;
import com.dalhousie.FundFusion.user.service.UserTransactionService;
import com.dalhousie.FundFusion.util.CustomResponseBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/user/transaction")
@RequiredArgsConstructor
@RestController
public class UserTransactionController {

    private final UserTransactionService userTransactionService;

    @GetMapping("/test")
    public String test(){
        return "Hello";
    }

    @PostMapping("/logTransaction")
    public ResponseEntity<CustomResponseBody<UserTransactionResponse>> logTransaction(@RequestBody @Valid UserTransactionRequest userTransactionRequest){
        CustomResponseBody<UserTransactionResponse> response = new CustomResponseBody<>(
                CustomResponseBody.Result.SUCCESS,
                userTransactionService.logTransaction(userTransactionRequest),
                "Transaction logged successfully"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/updateTransaction")
    public ResponseEntity<UserTransaction> updateTransaction(@RequestBody UserTransaction userTransaction){
        return  new ResponseEntity<>(userTransactionService.updateTransaction(userTransaction),HttpStatus.ACCEPTED);
    }

    @GetMapping("/getTransaction/{txnId}")
    public ResponseEntity<UserTransaction> getTransactionWithTxnId(@PathVariable Integer txnId){
        return new ResponseEntity<>(userTransactionService.getTransactionWithTxnId(txnId), HttpStatus.OK);
    }

    @GetMapping("/getTransactionsBetweenDate")
    public ResponseEntity<List<UserTransaction>> getTransactionsBetweenDate(@RequestBody DateRangeEntity dateRange){
        return new ResponseEntity<>(userTransactionService.getTransactionsWithinDateRange(dateRange.getFromDate(),dateRange.getToDate()), HttpStatus.OK);
    }

    @GetMapping("/getTransactionsWithCategory")
    public ResponseEntity<List<UserTransaction>> getTransactionsWithCategory(@RequestBody Category category){
        return new ResponseEntity<>(userTransactionService.getTransactionsWithCategory(category), HttpStatus.OK);
    }




}
