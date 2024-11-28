package com.dalhousie.FundFusion.splitBill.controller;

import com.dalhousie.FundFusion.splitBill.requestEntity.AddExpenseRequest;
import com.dalhousie.FundFusion.splitBill.requestEntity.SettleDebtRequest;
import com.dalhousie.FundFusion.splitBill.requestEntity.UpdateExpenseRequest;
import com.dalhousie.FundFusion.splitBill.responseEntity.*;
import com.dalhousie.FundFusion.splitBill.service.SplitBillService;
import com.dalhousie.FundFusion.util.CustomResponseBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("splitBill")
@RequiredArgsConstructor
public class SplitBillController {

    private final SplitBillService splitBillService;

    @PostMapping("/addExpense")
    public ResponseEntity<CustomResponseBody<AddExpenseResponse>> addExpense(@Valid @RequestBody AddExpenseRequest request) {
        try {
            log.info("Received request to add expense: {}", request);
            AddExpenseResponse response = splitBillService.createExpense(request);
            HttpStatus status = HttpStatus.CREATED;
            CustomResponseBody.Result result = CustomResponseBody.Result.SUCCESS;
            String message = "Expense added successfully";
            return buildResponse(status, result, response, message);
        } catch (IllegalArgumentException e) {
            log.error("Validation error while adding expense: {}", e.getMessage());
            HttpStatus status = HttpStatus.BAD_REQUEST;
            CustomResponseBody.Result result = CustomResponseBody.Result.FAILURE;
            String message = e.getMessage();
            return buildResponse(status, result, null, message);
        } catch (Exception e) {
            log.error("Unexpected error while adding expense: {}", e.getMessage());
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            CustomResponseBody.Result result = CustomResponseBody.Result.FAILURE;
            String message = "An unexpected error occurred while adding the expense";
            return buildResponse(status, result, null, message);
        }
    }

    @PutMapping("/updateExpense")
    public ResponseEntity<CustomResponseBody<AddExpenseResponse>> updateExpense(@Valid @RequestBody UpdateExpenseRequest request) {
        try {
            log.info("Received request to update expense: {}", request);
            AddExpenseResponse response = splitBillService.updateExpense(request);
            HttpStatus status = HttpStatus.OK;
            CustomResponseBody.Result result = CustomResponseBody.Result.SUCCESS;
            String message = "Expense updated successfully";
            return buildResponse(status, result, response, message);
        } catch (IllegalArgumentException e) {
            log.error("Validation error while updating expense: {}", e.getMessage());
            HttpStatus status = HttpStatus.BAD_REQUEST;
            CustomResponseBody.Result result = CustomResponseBody.Result.FAILURE;
            String message = e.getMessage();
            return buildResponse(status, result, null, message);
        } catch (Exception e) {
            log.error("Unexpected error while updating expense: {}", e.getMessage());
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            CustomResponseBody.Result result = CustomResponseBody.Result.FAILURE;
            String message = "An unexpected error occurred while updating the expense";
            return buildResponse(status, result, null, message);
        }
    }

    @DeleteMapping("/deleteExpense")
    public ResponseEntity<CustomResponseBody<String>> deleteExpense(@RequestParam Long transactionId) {
        try {
            log.info("Received request to delete expense with ID: {}", transactionId);
            splitBillService.deleteExpense(transactionId);
            HttpStatus status = HttpStatus.OK;
            CustomResponseBody.Result result = CustomResponseBody.Result.SUCCESS;
            String message = "Expense deleted successfully";
            return buildResponse(status, result, null, message);
        } catch (IllegalArgumentException e) {
            log.error("Validation error while deleting expense: {}", e.getMessage());
            HttpStatus status = HttpStatus.BAD_REQUEST;
            CustomResponseBody.Result result = CustomResponseBody.Result.FAILURE;
            String message = e.getMessage();
            return buildResponse(status, result, null, message);
        } catch (Exception e) {
            log.error("Unexpected error while deleting expense: {}", e.getMessage());
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            CustomResponseBody.Result result = CustomResponseBody.Result.FAILURE;
            String message = "An unexpected error occurred while deleting the expense";
            return buildResponse(status, result, null, message);
        }
    }

    @PostMapping("/settleDebt")
    public ResponseEntity<CustomResponseBody<String>> settleDebt(@Valid @RequestBody SettleDebtRequest request) {
        try {
            log.info("Received request to settle debt: {}", request);
            splitBillService.settleDebt(request);
            HttpStatus status = HttpStatus.OK;
            CustomResponseBody.Result result = CustomResponseBody.Result.SUCCESS;
            String message = "Debt settled successfully";
            return buildResponse(status, result, null, message);
        } catch (IllegalArgumentException e) {
            log.error("Validation error while settling debt: {}", e.getMessage());
            HttpStatus status = HttpStatus.BAD_REQUEST;
            CustomResponseBody.Result result = CustomResponseBody.Result.FAILURE;
            String message = e.getMessage();
            return buildResponse(status, result, null, message);
        } catch (Exception e) {
            log.error("Unexpected error while settling debt: {}", e.getMessage());
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            CustomResponseBody.Result result = CustomResponseBody.Result.FAILURE;
            String message = "An unexpected error occurred while settling the debt";
            return buildResponse(status, result, null, message);
        }
    }

    @GetMapping("/getDebts")
    public ResponseEntity<CustomResponseBody<List<DebtResponse>>> getDebts(@RequestParam Integer groupId) {
        try {
            log.info("Fetching debts for group ID: {}", groupId);
            List<DebtResponse> debts = splitBillService.getTransactionDebts(groupId);
            HttpStatus status = HttpStatus.OK;
            CustomResponseBody.Result result = CustomResponseBody.Result.SUCCESS;
            String message = "Debts fetched successfully";
            return buildResponse(status, result, debts, message);
        } catch (IllegalArgumentException e) {
            log.error("Validation error while fetching debts: {}", e.getMessage());
            HttpStatus status = HttpStatus.BAD_REQUEST;
            CustomResponseBody.Result result = CustomResponseBody.Result.FAILURE;
            String message = e.getMessage();
            return buildResponse(status, result, null, message);
        } catch (Exception e) {
            log.error("Unexpected error while fetching debts: {}", e.getMessage());
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            CustomResponseBody.Result result = CustomResponseBody.Result.FAILURE;
            String message = "An unexpected error occurred while fetching debts";
            return buildResponse(status, result, null, message);
        }
    }

    @GetMapping("/groupSummary")
    public ResponseEntity<CustomResponseBody<List<DebtResponse>>> getSimplifiedDebt(@RequestParam Integer groupId) {
        try {
            log.info("Fetching group summary for group ID: {}", groupId);
            List<DebtResponse> summary = splitBillService.getgroupdebtsummary(groupId);

            HttpStatus status = HttpStatus.OK;
            CustomResponseBody.Result result = CustomResponseBody.Result.SUCCESS;
            String message = "Group summary fetched successfully";
            return buildResponse(status, result, summary, message);
        } catch (IllegalArgumentException e) {
            log.error("Error fetching group summary: {}", e.getMessage());

            HttpStatus status = HttpStatus.BAD_REQUEST;
            CustomResponseBody.Result result = CustomResponseBody.Result.FAILURE;
            String message = e.getMessage();
            return buildResponse(status, result, null, message);
        } catch (Exception e) {
            log.error("Unexpected error fetching group summary: {}", e.getMessage());

            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            CustomResponseBody.Result result = CustomResponseBody.Result.FAILURE;
            String message = "An unexpected error occurred while fetching the group summary";
            return buildResponse(status, result, null, message);
        }
    }

    @GetMapping("/transactionSummary")
    public ResponseEntity<CustomResponseBody<List<TransactionSummaryResponse>>> getTransactionSummary(@RequestParam Integer groupId) {
        try {
            log.info("Fetching transaction summary for group ID: {}", groupId);
            List<TransactionSummaryResponse> summary = splitBillService.getTransactionSummary(groupId);

            HttpStatus status = HttpStatus.OK;
            CustomResponseBody.Result result = CustomResponseBody.Result.SUCCESS;
            String message = "Fetched transaction summary successfully";
            return buildResponse(status, result, summary, message);
        } catch (IllegalArgumentException e) {
            log.error("Error fetching transaction summary: {}", e.getMessage());

            HttpStatus status = HttpStatus.BAD_REQUEST;
            CustomResponseBody.Result result = CustomResponseBody.Result.FAILURE;
            String message = e.getMessage();
            return buildResponse(status, result, null, message);
        } catch (Exception e) {
            log.error("Unexpected error fetching transaction summary: {}", e.getMessage());

            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            CustomResponseBody.Result result = CustomResponseBody.Result.FAILURE;
            String message = "An unexpected error occurred while fetching the transaction summary";
            return buildResponse(status, result, null, message);
        }
    }

    @GetMapping("/userDebts")
    public ResponseEntity<CustomResponseBody<List<UserDebtResponse>>> getDebtsForAuthenticatedUserByGroup(@RequestParam Integer groupId) {
        try {
            log.info("Fetching debts for the authenticated user in group ID: {}", groupId);
            List<UserDebtResponse> debts = splitBillService.getDebtsForAuthenticatedUserByGroup(groupId);

            HttpStatus status = HttpStatus.OK;
            CustomResponseBody.Result result = CustomResponseBody.Result.SUCCESS;
            String message = "Fetched debts successfully";
            return buildResponse(status, result, debts, message);
        } catch (IllegalArgumentException e) {
            log.error("Error fetching debts: {}", e.getMessage());

            HttpStatus status = HttpStatus.BAD_REQUEST;
            CustomResponseBody.Result result = CustomResponseBody.Result.FAILURE;
            String message = e.getMessage();
            return buildResponse(status, result, null, message);
        } catch (Exception e) {
            log.error("Unexpected error fetching debts: {}", e.getMessage());

            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            CustomResponseBody.Result result = CustomResponseBody.Result.FAILURE;
            String message = "An unexpected error occurred while fetching debts";
            return buildResponse(status, result, null, message);
        }
    }

    @GetMapping("/debitCreditSummary")
    public ResponseEntity<CustomResponseBody<DebitCreditSummaryResponse>> getDebitCreditSummary(@RequestParam Integer groupId) {
        try {
            log.info("Fetching debit-credit summary for the authenticated user.");
            DebitCreditSummaryResponse summary = splitBillService.getDebitCreditSummary(groupId);

            HttpStatus status = HttpStatus.OK;
            CustomResponseBody.Result result = CustomResponseBody.Result.SUCCESS;
            String message = "Fetched debit-credit summary successfully";
            return buildResponse(status, result, summary, message);
        } catch (Exception e) {
            log.error("Error fetching debit-credit summary: {}", e.getMessage());

            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            CustomResponseBody.Result result = CustomResponseBody.Result.FAILURE;
            String message = "Failed to fetch debit-credit summary";
            return buildResponse(status, result, null, message);
        }
    }

     private <T> ResponseEntity<CustomResponseBody<T>> buildResponse(HttpStatus status, CustomResponseBody.Result result, T data, String message) {
        CustomResponseBody<T> responseBody = new CustomResponseBody<>(result, data, message);
        return ResponseEntity.status(status).body(responseBody);
    }
}
