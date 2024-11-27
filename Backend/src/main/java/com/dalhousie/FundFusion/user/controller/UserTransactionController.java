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

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@RequestMapping("/user/transaction")
@RequiredArgsConstructor
@Slf4j
@RestController
public class UserTransactionController {

    private final UserTransactionService userTransactionService;

    @PostMapping("/logTransaction")
    public ResponseEntity<CustomResponseBody<UserTransactionResponse>> logTransaction(@RequestBody UserTransactionRequest userTransactionRequest) {
        try {
            // Prepare response body
            UserTransactionResponse transactionResponse = userTransactionService.logTransaction(userTransactionRequest);
            HttpStatus status = HttpStatus.CREATED;
            CustomResponseBody.Result result = CustomResponseBody.Result.SUCCESS;
            String message = "Transaction logged successfully";
            return buildResponse(status, result, transactionResponse, message);
        } catch (Exception e) {
            log.error("Unexpected error during transaction: {}", e.getMessage());
            HttpStatus status = HttpStatus.BAD_REQUEST;
            CustomResponseBody.Result result = CustomResponseBody.Result.FAILURE;
            String message = "Something went wrong";
            return buildResponse(status, result, null, message);
        }
    }

    @PutMapping("/updateTransaction")
    public ResponseEntity<CustomResponseBody<UserTransactionResponse>> updateTransaction(@RequestBody UserTransactionRequest userTransactionRequest) {
        try {
            // Prepare response body
            UserTransactionResponse transactionResponse = userTransactionService.updateTransaction(userTransactionRequest);
            HttpStatus status = HttpStatus.CREATED;
            CustomResponseBody.Result result = CustomResponseBody.Result.SUCCESS;
            String message = "Transaction updated successfully";
            return buildResponse(status, result, transactionResponse, message);
        } catch (Exception e) {
            log.error("Unexpected error during transaction: {}", e.getMessage());
            HttpStatus status = HttpStatus.BAD_REQUEST;
            CustomResponseBody.Result result = CustomResponseBody.Result.FAILURE;
            String message = "Something went wrong";
            return buildResponse(status, result, null, message);
        }
    }

    @GetMapping("/getAllTransactions")
    public ResponseEntity<CustomResponseBody<List<UserTransactionResponse>>> getAllTransactions() {
        try {
            // Prepare response body
            List<UserTransactionResponse> transactions = userTransactionService.getAllTransactions();
            HttpStatus status = HttpStatus.CREATED;
            CustomResponseBody.Result result = CustomResponseBody.Result.SUCCESS;
            String message = "All transactions fetched successfully";
            return buildResponse(status, result, transactions, message);
        } catch (Exception e) {
            log.error("Unexpected error during transaction: {}", e.getMessage());
            HttpStatus status = HttpStatus.BAD_REQUEST;
            CustomResponseBody.Result result = CustomResponseBody.Result.FAILURE;
            String message = "Something went wrong";
            return buildResponse(status, result, null, message);
        }
    }

    @DeleteMapping("/deleteTransaction")
    public ResponseEntity<CustomResponseBody<UserTransactionResponse>> deleteTransaction(@RequestParam("id") Integer id) {
        try {
            // Delete transaction
            userTransactionService.deleteTransaction(id);
            HttpStatus status = HttpStatus.CREATED;
            CustomResponseBody.Result result = CustomResponseBody.Result.SUCCESS;
            String message = "Transaction deleted successfully";
            return buildResponse(status, result, null, message);
        } catch (NoSuchElementException e) {
            log.error("Unexpected error during transaction: {}", e.getMessage());
            HttpStatus status = HttpStatus.BAD_REQUEST;
            CustomResponseBody.Result result = CustomResponseBody.Result.FAILURE;
            String message = "Transaction not found";
            return buildResponse(status, result, null, message);
        }
    }

    @GetMapping("/getTransactionsBetweenDate")
    public ResponseEntity<CustomResponseBody<List<UserTransactionResponse>>> getTransactionsBetweenDate(@RequestParam String fromDate, @RequestParam String toDate) {
        try {
            // Create DateRangeEntity object
            DateRangeEntity dateRange = new DateRangeEntity();
            dateRange.setFromDate(LocalDate.parse(fromDate));
            dateRange.setToDate(LocalDate.parse(toDate));

            // Fetch transactions within the date range
            List<UserTransactionResponse> transactions = userTransactionService.getTransactionsWithinDateRange(dateRange);
            HttpStatus status = HttpStatus.OK;
            CustomResponseBody.Result result = CustomResponseBody.Result.SUCCESS;
            String message = "Transaction within daterange fetched successfully";
            return buildResponse(status, result, transactions, message);
        } catch (NoSuchElementException e) {
            log.error("Unexpected error during transaction: {}", e.getMessage());
            HttpStatus status = HttpStatus.BAD_REQUEST;
            CustomResponseBody.Result result = CustomResponseBody.Result.FAILURE;
            String message = "Something went wrong";
            return buildResponse(status, result, null, message);
        }
    }

    @GetMapping("/getTransactionsWithCategory")
    public ResponseEntity<CustomResponseBody<List<UserTransactionResponse>>> getTransactionsWithCategory(@RequestParam Integer categoryId) {
        try {
            // Create UserTransactionRequest from categoryId
            UserTransactionRequest request = new UserTransactionRequest();
            request.setCategoryId(categoryId);

            // Fetch transactions with category
            List<UserTransactionResponse> transactions = userTransactionService.getTransactionsWithCategory(request);
            HttpStatus status = HttpStatus.OK;
            CustomResponseBody.Result result = CustomResponseBody.Result.SUCCESS;
            String message = "Transaction with category fetched successfully";
            return buildResponse(status, result, transactions, message);
        } catch (NoSuchElementException e) {
            log.error("Unexpected error during transaction: {}", e.getMessage());
            HttpStatus status = HttpStatus.BAD_REQUEST;
            CustomResponseBody.Result result = CustomResponseBody.Result.FAILURE;
            String message = "Something went wrong";
            return buildResponse(status, result, null, message);
        }
    }

    // Helper method to build the response
    private <T> ResponseEntity<CustomResponseBody<T>> buildResponse(HttpStatus status, CustomResponseBody.Result result, T data, String message) {
        CustomResponseBody<T> responseBody = new CustomResponseBody<>(result, data, message);
        return ResponseEntity.status(status).body(responseBody);
    }
}
