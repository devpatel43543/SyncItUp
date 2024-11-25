package com.dalhousie.FundFusion.splitBill.controller;
import com.dalhousie.FundFusion.splitBill.requestEntity.AddExpenseRequest;
import com.dalhousie.FundFusion.splitBill.responseEntity.DebitCreditSummaryResponse;
import com.dalhousie.FundFusion.splitBill.requestEntity.SettleDebtRequest;
import com.dalhousie.FundFusion.splitBill.requestEntity.UpdateExpenseRequest;
import com.dalhousie.FundFusion.splitBill.responseEntity.*;
import com.dalhousie.FundFusion.splitBill.service.SplitBillService;
import com.dalhousie.FundFusion.util.CustomResponseBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("splitBill")
@RequiredArgsConstructor
public class SplitBillController {

    private final SplitBillService splitBillService;

    /**
     * Create a new expense
     */
    @PostMapping("/addExpense")
    public ResponseEntity<CustomResponseBody<AddExpenseResponse>> addExpense(@Valid @RequestBody AddExpenseRequest request) {
        try {
            log.info("Received request to add expense: {}", request);
            AddExpenseResponse response = splitBillService.createExpense(request);

            CustomResponseBody<AddExpenseResponse> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.SUCCESS,
                    response,
                    "Expense added successfully"
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
        } catch (IllegalArgumentException e) {
            log.error("Validation error while adding expense: {}", e.getMessage());
            CustomResponseBody<AddExpenseResponse> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        } catch (Exception e) {
            log.error("Unexpected error while adding expense: {}", e.getMessage());
            CustomResponseBody<AddExpenseResponse> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    "An unexpected error occurred while adding the expense"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
        }
    }

    /**
     * Update an existing expense
     */
    @PutMapping("/updateExpense")
    public ResponseEntity<CustomResponseBody<AddExpenseResponse>> updateExpense(@Valid @RequestBody UpdateExpenseRequest request) {
        try {
            log.info("Received request to update expense: {}", request);
            AddExpenseResponse response = splitBillService.updateExpense(request);

            CustomResponseBody<AddExpenseResponse> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.SUCCESS,
                    response,
                    "Expense updated successfully"
            );
            return ResponseEntity.ok(responseBody);
        } catch (IllegalArgumentException e) {
            log.error("Validation error while updating expense: {}", e.getMessage());
            CustomResponseBody<AddExpenseResponse> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        } catch (Exception e) {
            log.error("Unexpected error while updating expense: {}", e.getMessage());
            CustomResponseBody<AddExpenseResponse> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    "An unexpected error occurred while updating the expense"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
        }
    }

    /**
     * Delete an expense by ID
     */
    @DeleteMapping("/deleteExpense")
    public ResponseEntity<CustomResponseBody<String>> deleteExpense(@RequestParam Long transactionId) {
        try {
            log.info("Received request to delete expense with ID: {}", transactionId);
            splitBillService.deleteExpense(transactionId);

            CustomResponseBody<String> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.SUCCESS,
                    null,
                    "Expense deleted successfully"
            );
            return ResponseEntity.ok(responseBody);
        } catch (IllegalArgumentException e) {
            log.error("Validation error while deleting expense: {}", e.getMessage());
            CustomResponseBody<String> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        } catch (Exception e) {
            log.error("Unexpected error while deleting expense: {}", e.getMessage());
            CustomResponseBody<String> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    "An unexpected error occurred while deleting the expense"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
        }
    }

    /*
    * {
    "result": "SUCCESS",
    "data": [
        {
            "paidByEmail": "devpatel43543@gmail.com",
            "owesToEmail": "devilp164@gmail.com",
            "amount": 50.0
        },
        {
            "paidByEmail": "devpatel43543@gmail.com",
            "owesToEmail": "jems007patel@gmail.com",
            "amount": 50.0
        },
        {
            "paidByEmail": "devpatel43543@gmail.com",
            "owesToEmail": "vedant7978@gmail.com",
            "amount": 50.0
        },
        {
            "paidByEmail": "devpatel43543@gmail.com",
            "owesToEmail": "devilp164@gmail.com",
            "amount": 50.0
        },
        {
            "paidByEmail": "devpatel43543@gmail.com",
            "owesToEmail": "vedant7978@gmail.com",
            "amount": 50.0
        }
    ],
    "message": "Debts fetched successfully"
}
    * */
    @GetMapping("/getDebts")
    public ResponseEntity<CustomResponseBody<List<DebtResponse>>> getDebts(@RequestParam Integer groupId) {
        try {
            log.info("Fetching debts for the authenticated user in group ID: {}", groupId);

            // Fetch debts for the authenticated user in the specified group
            List<DebtResponse> debts = splitBillService.getTransactionDebts(groupId);

            // Return success response
            CustomResponseBody<List<DebtResponse>> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.SUCCESS,
                    debts,
                    "Debts fetched successfully"
            );
            return ResponseEntity.ok(responseBody);
        } catch (IllegalArgumentException e) {
            log.error("Validation error while fetching debts: {}", e.getMessage());
            CustomResponseBody<List<DebtResponse>> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        } catch (Exception e) {
            log.error("Unexpected error while fetching debts: {}", e.getMessage());
            CustomResponseBody<List<DebtResponse>> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    "An unexpected error occurred while fetching debts"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
        }
    }

    /*
    * {
    "result": "SUCCESS",
    "data": [
        {
            "paidByEmail": "devpatel43543@gmail.com",
            "owesToEmail": "devilp164@gmail.com",
            "amount": 100.0
        },
        {
            "paidByEmail": "devpatel43543@gmail.com",
            "owesToEmail": "jems007patel@gmail.com",
            "amount": 50.0
        },
        {
            "paidByEmail": "devpatel43543@gmail.com",
            "owesToEmail": "vedant7978@gmail.com",
            "amount": 100.0
        },
        {
            "paidByEmail": "devilp164@gmail.com",
            "owesToEmail": "devpatel43543@gmail.com",
            "amount": 50.0
        },
        {
            "paidByEmail": "devilp164@gmail.com",
            "owesToEmail": "vedant7978@gmail.com",
            "amount": 50.0
        }
    ],
    "message": "Group summary fetched successfully"
}
    * */
    @GetMapping("/groupSummary")
    public ResponseEntity<CustomResponseBody<List<DebtResponse>>> getSimplifiedDebt(@RequestParam Integer groupId) {
        try {
            log.info("Fetching group summary for group ID: {}", groupId);

            // Fetch group partnership summary
            List<DebtResponse> summary = splitBillService.getgroupdebtsummary(groupId);

            // Prepare response
            CustomResponseBody<List<DebtResponse>> response = new CustomResponseBody<>(
                    CustomResponseBody.Result.SUCCESS,
                    summary,
                    "Group summary fetched successfully"
            );

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Error fetching group summary: {}", e.getMessage());

            // Prepare error response
            CustomResponseBody<List<DebtResponse>> response = new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    e.getMessage()
            );

            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Unexpected error fetching group summary: {}", e.getMessage());

            // Prepare error response
            CustomResponseBody<List<DebtResponse>> response = new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    "An unexpected error occurred while fetching the group summary"
            );

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    //to show in the home page of inside group dashboard
    /*
    {
        "result": "FAILURE",
            "data": [
        {
            "amountPaid": 150.0,
                "transactionDate": "2024-11-20T19:19:42.381317",
                "paidByEmail": "devpatel43543@gmail.com",
                "involvedMembersCount": 3,
                "involvedMembers": [
            "devilp164@gmail.com",
                    "devpatel43543@gmail.com",
                    "vedant7978@gmail.com"
            ],
            "description": "Snacks"
        },
    }
    */
    @GetMapping("/transactionSummary")
    public ResponseEntity<CustomResponseBody<List<TransactionSummaryResponse>>> getTransactionSummary(@RequestParam Integer groupId){
        try {
            log.info("Fetching group summary for group ID: {}", groupId);

            // Fetch group partnership summary
            List<TransactionSummaryResponse> summary = splitBillService.getTransactionSummary(groupId);

            // Prepare response
            CustomResponseBody response = new CustomResponseBody<>(
                    CustomResponseBody.Result.SUCCESS,
                    summary,
                    "fetched successfully"
            );

            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e) {
            log.error("Error fetching group summary: {}", e.getMessage());

            // Prepare error response
            CustomResponseBody<List<TransactionSummaryResponse>> response = new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    e.getMessage()
            );

            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Unexpected error fetching transaction summary: {}", e.getMessage());

            // Prepare error response
            CustomResponseBody<List<TransactionSummaryResponse>> response = new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    "An unexpected error occurred while fetching the group summary"
            );

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // fetch debts for the authenticated user in a group
    /*
    * {
    "result": "SUCCESS",
    "data": [
        {
            "creditorEmail": "devpatel43543@gmail.com",
            "groupId": 7,
            "groupName": "1881 testing",
            "amount": 100.0
        }
    ],
    "message": "Fetched debts successfully."
    }*/
    @GetMapping("/userDebts")
    public ResponseEntity<CustomResponseBody<List<UserDebtResponse>>> getDebtsForAuthenticatedUserByGroup(@RequestParam Integer groupId) {
        try {
            log.info("Fetching debts for the authenticated user in group ID: {}", groupId);

            // Fetch debts
            List<UserDebtResponse> debts = splitBillService.getDebtsForAuthenticatedUserByGroup(groupId);

            // Prepare response
            CustomResponseBody<List<UserDebtResponse>> response = new CustomResponseBody<>(
                    CustomResponseBody.Result.SUCCESS,
                    debts,
                    "Fetched debts successfully."
            );

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Error fetching debts: {}", e.getMessage());

            // Prepare error response
            CustomResponseBody<List<UserDebtResponse>> response = new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    e.getMessage()
            );

            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Unexpected error fetching debts: {}", e.getMessage());

            // Prepare error response
            CustomResponseBody<List<UserDebtResponse>> response = new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    "An unexpected error occurred while fetching debts."
            );

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // settle a specific debt
    /*
      input
        private Integer groupId;
        private String creditorEmail;
        private Double amount;
    * */
    @PostMapping("/settleDebt")
    public ResponseEntity<CustomResponseBody<SettlementResponse>> settleDebt(@RequestBody SettleDebtRequest request) {
        try {
            log.info("Settling debt with creditor email: {} for group ID: {}", request.getCreditorEmail(), request.getGroupId());

            // Perform settlement
            SettlementResponse settlementResponse = splitBillService.settleDebt(request);

            // Prepare response
            CustomResponseBody<SettlementResponse> response = new CustomResponseBody<>(
                    CustomResponseBody.Result.SUCCESS,
                    settlementResponse,
                    "Debt settled successfully."
            );

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Error settling debt: {}", e.getMessage());

            // Prepare error response
            CustomResponseBody<SettlementResponse> response = new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    e.getMessage()
            );

            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Unexpected error settling debt: {}", e.getMessage());

            // Prepare error response
            CustomResponseBody<SettlementResponse> response = new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    "An unexpected error occurred while settling the debt."
            );

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /*
    * {
    "result": "SUCCESS",
    "data": {
        "userEmail": "devpatel43543@gmail.com",
        "totalDebit": 50.0,
        "totalCredit": 150.0
    },
    "message": "Fetched debit-credit summary successfully."
    }
    * */
    @GetMapping("/debitCreditSummary")
    public ResponseEntity<CustomResponseBody<DebitCreditSummaryResponse>> getDebitCreditSummary(@RequestParam Integer groupId) {
        try {
            log.info("Fetching debit-credit summary for the authenticated user.");
            DebitCreditSummaryResponse summary = splitBillService.getDebitCreditSummary(groupId);

            // Prepare and return the success response
            return ResponseEntity.ok(new CustomResponseBody<>(
                    CustomResponseBody.Result.SUCCESS,
                    summary,
                    "Fetched debit-credit summary successfully."
            ));
        } catch (Exception e) {
            log.error("Error fetching debit-credit summary: {}", e.getMessage());

            // Prepare and return the error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    "Failed to fetch debit-credit summary."
            ));
        }
    }
}
