package com.dalhousie.FundFusion.user.controller;

import com.dalhousie.FundFusion.dto.DateRangeEntity;
import com.dalhousie.FundFusion.user.entity.User;
import com.dalhousie.FundFusion.user.requestEntity.UserTransactionRequest;
import com.dalhousie.FundFusion.user.responseEntity.UserTransactionResponse;
import com.dalhousie.FundFusion.user.service.UserTransactionService;
import com.dalhousie.FundFusion.util.CustomResponseBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RequestMapping("/user/transaction")
@RequiredArgsConstructor
@Slf4j
@RestController
public class UserTransactionController {

    private final UserTransactionService userTransactionService;

    @PostMapping("/logTransaction")
    public ResponseEntity<CustomResponseBody<UserTransactionResponse>> logTransaction(@RequestBody UserTransactionRequest userTransactionRequest){
        try{
            //UserTransactionResponse userTransactionResponse = userTransactionService.logTransaction(userTransactionRequest);
            //log.info(userTransactionResponse.toString());
            CustomResponseBody<UserTransactionResponse> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.SUCCESS,
                    userTransactionService.logTransaction(userTransactionRequest),
                    "Transaction logged successfully"
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
        }
        catch (Exception e){
            log.error("Unexpected error during transaction: {}",e.getMessage());
            CustomResponseBody<UserTransactionResponse> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    "Something went wrong");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        }
    }

    @PutMapping("/updateTransaction")
    public ResponseEntity<CustomResponseBody<UserTransactionResponse>> updateTransaction(@RequestBody UserTransactionRequest userTransactionRequest){
        try{
            CustomResponseBody<UserTransactionResponse> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.SUCCESS,
                    userTransactionService.updateTransaction(userTransactionRequest),
                    "Transaction updated successfully"
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
        }
        catch (Exception e){
            log.error("Unexpected error during transaction: {}",e.getMessage());
            CustomResponseBody<UserTransactionResponse> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    "Something went wrong");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        }
    }

    @GetMapping("/getAllTransactions")
    public ResponseEntity<CustomResponseBody<List<UserTransactionResponse>>> getAllTransactions(){
        try{
            CustomResponseBody<List<UserTransactionResponse>> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.SUCCESS,
                    userTransactionService.getAllTransactions(),
                    "All transactions fetched successfully"
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
        }
        catch (Exception e){
            log.error("Unexpected error during transaction: {}",e.getMessage());
            CustomResponseBody<List<UserTransactionResponse>> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    "Something went wrong");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        }
    }

    @DeleteMapping("/deleteTransaction")
    public ResponseEntity<CustomResponseBody<UserTransactionResponse>> deleteTransaction(@RequestParam("id") Integer id){
        try{
            userTransactionService.deleteTransaction(id);
            CustomResponseBody<UserTransactionResponse> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.SUCCESS,
                    null,
                    "Transaction deleted successfully"
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
        }
        catch (NoSuchElementException e){
            log.error("Unexpected error during transaction: {}",e.getMessage());
            CustomResponseBody<UserTransactionResponse> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    "Transaction not found"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        }
    }


    @GetMapping("/getTransactionsBetweenDate")
    public ResponseEntity<CustomResponseBody<List<UserTransactionResponse>>> getTransactionsBetweenDate(@RequestBody DateRangeEntity dateRange){
        try{

            CustomResponseBody<List<UserTransactionResponse>> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.SUCCESS,
                    userTransactionService.getTransactionsWithinDateRange(dateRange),
                    "Transaction within daterange fetched successfully"
            );
            return ResponseEntity.status(HttpStatus.OK).body(responseBody);
        }
        catch (NoSuchElementException e){
            log.error("Unexpected error during transaction: {}",e.getMessage());
            CustomResponseBody<List<UserTransactionResponse>> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    "Something went wrong");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        }
    }

    @GetMapping("/getTransactionsWithCategory")
    public ResponseEntity<CustomResponseBody<List<UserTransactionResponse>>> getTransactionsWithCategory(@RequestBody UserTransactionRequest request){
        try{

            CustomResponseBody<List<UserTransactionResponse>> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.SUCCESS,
                    userTransactionService.getTransactionsWithCategory(request),
                    "Transaction with category fetched successfully"
            );
            return ResponseEntity.status(HttpStatus.OK).body(responseBody);
        }
        catch (NoSuchElementException e){
            log.error("Unexpected error during transaction: {}",e.getMessage());
            CustomResponseBody<List<UserTransactionResponse>> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    "Something went wrong");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        }
    }




}
