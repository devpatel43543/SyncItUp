package com.dalhousie.FundFusion.controller.splitBill;

import com.dalhousie.FundFusion.splitBill.controller.SplitBillController;
import com.dalhousie.FundFusion.splitBill.requestEntity.*;
import com.dalhousie.FundFusion.splitBill.responseEntity.*;
import com.dalhousie.FundFusion.splitBill.service.SplitBillService;
import com.dalhousie.FundFusion.util.CustomResponseBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

class SplitBillControllerTest {

    @Test
    void testAddExpense_Success() {

        SplitBillService mockService = Mockito.mock(SplitBillService.class);

        AddExpenseResponse mockResponse = AddExpenseResponse.builder()
                .transactionId(1001L)
                .groupName("Group A")
                .totalAmount(200.0)
                .paidByEmail("payer@example.com")
                .title("Dinner")
                .category("Food")
                .build();

        Mockito.when(mockService.createExpense(Mockito.any(AddExpenseRequest.class)))
                .thenReturn(mockResponse);

        SplitBillController controller = new SplitBillController(mockService);

        AddExpenseRequest request = new AddExpenseRequest();
        request.setGroupId(1);
        request.setPaidByEmail("payer@example.com");
        request.setAmount(200.0);
        request.setInvolvedMembers(List.of("member1@example.com", "member2@example.com"));

        ResponseEntity<CustomResponseBody<AddExpenseResponse>> response = controller.addExpense(request);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertEquals("Expense added successfully", response.getBody().message());

        AddExpenseResponse responseData = response.getBody().data();
        Assertions.assertNotNull(responseData);
        Assertions.assertEquals(1001L, responseData.getTransactionId());
        Assertions.assertEquals("Group A", responseData.getGroupName());
        Assertions.assertEquals(200.0, responseData.getTotalAmount());
        Assertions.assertEquals("payer@example.com", responseData.getPaidByEmail());
        Assertions.assertEquals("Food", responseData.getCategory());
    }


    @Test
    void testGetDebts_Success() {
        SplitBillService mockService = Mockito.mock(SplitBillService.class);

        List<DebtResponse> mockDebts = List.of(
                DebtResponse.builder()
                        .paidByEmail("payer1@example.com")
                        .owesToEmail("owed1@example.com")
                        .amount(50.0)
                        .build(),
                DebtResponse.builder()
                        .paidByEmail("payer2@example.com")
                        .owesToEmail("owed2@example.com")
                        .amount(75.0)
                        .build()
        );

        Mockito.when(mockService.getTransactionDebts(Mockito.anyInt())).thenReturn(mockDebts);

        SplitBillController controller = new SplitBillController(mockService);

        ResponseEntity<CustomResponseBody<List<DebtResponse>>> response = controller.getDebts(1);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertEquals("Debts fetched successfully", response.getBody().message());
        Assertions.assertEquals(2, response.getBody().data().size());

        DebtResponse debt1 = response.getBody().data().get(0);
        Assertions.assertEquals("payer1@example.com", debt1.getPaidByEmail());
        Assertions.assertEquals("owed1@example.com", debt1.getOwesToEmail());
        Assertions.assertEquals(50.0, debt1.getAmount());

        DebtResponse debt2 = response.getBody().data().get(1);
        Assertions.assertEquals("payer2@example.com", debt2.getPaidByEmail());
        Assertions.assertEquals("owed2@example.com", debt2.getOwesToEmail());
        Assertions.assertEquals(75.0, debt2.getAmount());
    }

    @Test
    void testSettleDebt_Success() {
        SplitBillService mockService = Mockito.mock(SplitBillService.class);

        SettlementResponse mockResponse = SettlementResponse.builder()
                .debtorEmail("debtor@example.com")
                .creditorEmail("creditor@example.com")
                .amountSettled(100.0)
                .message("Debt settled successfully.")
                .build();

        Mockito.when(mockService.settleDebt(Mockito.any(SettleDebtRequest.class)))
                .thenReturn(mockResponse);

        SplitBillController controller = new SplitBillController(mockService);

        SettleDebtRequest request = new SettleDebtRequest();
        request.setGroupId(1);
        request.setCreditorEmail("creditor@example.com");
        request.setAmount(100.0);

        ResponseEntity<CustomResponseBody<String>> response = controller.settleDebt(request);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertEquals("Debt settled successfully", response.getBody().message());
    }

    @Test
    void testDeleteExpense_Success() {
        SplitBillService mockService = Mockito.mock(SplitBillService.class);

        Mockito.doNothing().when(mockService).deleteExpense(Mockito.anyLong());

        SplitBillController controller = new SplitBillController(mockService);

        ResponseEntity<CustomResponseBody<String>> response = controller.deleteExpense(1L);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertEquals("Expense deleted successfully", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testGetTransactionSummary_Success() {
        SplitBillService mockService = Mockito.mock(SplitBillService.class);

        List<TransactionSummaryResponse> mockSummary = List.of(
                TransactionSummaryResponse.builder()
                        .transactionId(1001L)
                        .amountPaid(150.0)
                        .transactionDate(LocalDateTime.of(2024, 11, 20, 19, 30))
                        .paidByEmail("payer@example.com")
                        .involvedMembersCount(3)
                        .involvedMembers(List.of("member1@example.com", "member2@example.com"))
                        .title("Dinner")
                        .category("Food")
                        .build()
        );

        Mockito.when(mockService.getTransactionSummary(Mockito.anyInt())).thenReturn(mockSummary);

        SplitBillController controller = new SplitBillController(mockService);

        ResponseEntity<CustomResponseBody<List<TransactionSummaryResponse>>> response = controller.getTransactionSummary(1);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertEquals(1, response.getBody().data().size());

        TransactionSummaryResponse transaction = response.getBody().data().get(0);
        Assertions.assertEquals(1001L, transaction.getTransactionId());
        Assertions.assertEquals(150.0, transaction.getAmountPaid());
        Assertions.assertEquals(LocalDateTime.of(2024, 11, 20, 19, 30), transaction.getTransactionDate());
        Assertions.assertEquals("payer@example.com", transaction.getPaidByEmail());
        Assertions.assertEquals(3, transaction.getInvolvedMembersCount());
        Assertions.assertEquals(2, transaction.getInvolvedMembers().size());
        Assertions.assertTrue(transaction.getInvolvedMembers().contains("member1@example.com"));
        Assertions.assertTrue(transaction.getInvolvedMembers().contains("member2@example.com"));
        Assertions.assertEquals("Dinner", transaction.getTitle());
        Assertions.assertEquals("Food", transaction.getCategory());
    }

    @Test
    void testAddExpense_ValidationError() {
        SplitBillService mockService = Mockito.mock(SplitBillService.class);

        Mockito.when(mockService.createExpense(Mockito.any(AddExpenseRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid request data"));

        SplitBillController controller = new SplitBillController(mockService);

        AddExpenseRequest request = new AddExpenseRequest();
        request.setGroupId(null);

        ResponseEntity<CustomResponseBody<AddExpenseResponse>> response = controller.addExpense(request);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("Invalid request data", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testDeleteExpense_NotFound() {

        SplitBillService mockService = Mockito.mock(SplitBillService.class);

        Mockito.doThrow(new IllegalArgumentException("Expense not found"))
                .when(mockService)
                .deleteExpense(Mockito.anyLong());

        SplitBillController controller = new SplitBillController(mockService);

        ResponseEntity<CustomResponseBody<String>> response = controller.deleteExpense(999L);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("Expense not found", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testGetDebts_NoDebts() {

        SplitBillService mockService = Mockito.mock(SplitBillService.class);

        Mockito.when(mockService.getTransactionDebts(Mockito.anyInt())).thenReturn(List.of());

        SplitBillController controller = new SplitBillController(mockService);

        ResponseEntity<CustomResponseBody<List<DebtResponse>>> response = controller.getDebts(1);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertEquals("Debts fetched successfully", response.getBody().message());
        Assertions.assertTrue(response.getBody().data().isEmpty());
    }

    @Test
    void testSettleDebt_InsufficientAmount() {

        SplitBillService mockService = Mockito.mock(SplitBillService.class);

        Mockito.when(mockService.settleDebt(Mockito.any(SettleDebtRequest.class)))
                .thenThrow(new IllegalArgumentException("Insufficient amount to settle the debt"));

        SplitBillController controller = new SplitBillController(mockService);

        SettleDebtRequest request = new SettleDebtRequest();
        request.setGroupId(1);
        request.setCreditorEmail("creditor@example.com");
        request.setAmount(10.0);

        ResponseEntity<CustomResponseBody<String>> response = controller.settleDebt(request);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("Insufficient amount to settle the debt", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testGetTransactionSummary_Empty() {

        SplitBillService mockService = Mockito.mock(SplitBillService.class);

        Mockito.when(mockService.getTransactionSummary(Mockito.anyInt())).thenReturn(List.of());

        SplitBillController controller = new SplitBillController(mockService);

        ResponseEntity<CustomResponseBody<List<TransactionSummaryResponse>>> response = controller.getTransactionSummary(1);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertTrue(response.getBody().data().isEmpty());
    }

    @Test
    void testAddExpense_UnexpectedError() {
        SplitBillService mockService = Mockito.mock(SplitBillService.class);

        Mockito.when(mockService.createExpense(Mockito.any(AddExpenseRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error occurred"));

        SplitBillController controller = new SplitBillController(mockService);

        AddExpenseRequest request = new AddExpenseRequest();
        request.setGroupId(1);
        request.setPaidByEmail("payer@example.com");
        request.setAmount(200.0);
        request.setInvolvedMembers(List.of("member1@example.com"));

        ResponseEntity<CustomResponseBody<AddExpenseResponse>> response = controller.addExpense(request);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("An unexpected error occurred while adding the expense", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testUpdateExpense_Success() {
        SplitBillService mockService = Mockito.mock(SplitBillService.class);

        AddExpenseResponse mockResponse = AddExpenseResponse.builder()
                .transactionId(1002L)
                .groupName("Updated Group")
                .totalAmount(300.0)
                .paidByEmail("updater@example.com")
                .title("Updated Dinner")
                .category("Updated Food")
                .build();

        Mockito.when(mockService.updateExpense(Mockito.any(UpdateExpenseRequest.class)))
                .thenReturn(mockResponse);

        SplitBillController controller = new SplitBillController(mockService);

        UpdateExpenseRequest request = new UpdateExpenseRequest();
        request.setTransactionId(1002L);
        request.setAmount(300.0);

        ResponseEntity<CustomResponseBody<AddExpenseResponse>> response = controller.updateExpense(request);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertEquals("Expense updated successfully", response.getBody().message());
        Assertions.assertNotNull(response.getBody().data());
        Assertions.assertEquals(300.0, response.getBody().data().getTotalAmount());
    }

    @Test
    void testUpdateExpense_ValidationError() {
        SplitBillService mockService = Mockito.mock(SplitBillService.class);

        Mockito.when(mockService.updateExpense(Mockito.any(UpdateExpenseRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid update request"));

        SplitBillController controller = new SplitBillController(mockService);

        UpdateExpenseRequest request = new UpdateExpenseRequest();
        request.setTransactionId(null);

        ResponseEntity<CustomResponseBody<AddExpenseResponse>> response = controller.updateExpense(request);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("Invalid update request", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testUpdateExpense_UnexpectedError() {
        SplitBillService mockService = Mockito.mock(SplitBillService.class);

        Mockito.when(mockService.updateExpense(Mockito.any(UpdateExpenseRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error occurred"));

        SplitBillController controller = new SplitBillController(mockService);

        UpdateExpenseRequest request = new UpdateExpenseRequest();
        request.setTransactionId(1002L);
        request.setAmount(300.0);

        ResponseEntity<CustomResponseBody<AddExpenseResponse>> response = controller.updateExpense(request);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("An unexpected error occurred while updating the expense", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testDeleteExpense_UnexpectedError() {
        SplitBillService mockService = Mockito.mock(SplitBillService.class);

        Mockito.doThrow(new RuntimeException("Unexpected error occurred"))
                .when(mockService)
                .deleteExpense(Mockito.anyLong());

        SplitBillController controller = new SplitBillController(mockService);

        ResponseEntity<CustomResponseBody<String>> response = controller.deleteExpense(999L);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("An unexpected error occurred while deleting the expense", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testGetDebtsForAuthenticatedUserByGroup_NoDebts() {
        SplitBillService mockService = Mockito.mock(SplitBillService.class);

        Mockito.when(mockService.getDebtsForAuthenticatedUserByGroup(Mockito.anyInt()))
                .thenReturn(List.of());

        SplitBillController controller = new SplitBillController(mockService);

        ResponseEntity<CustomResponseBody<List<UserDebtResponse>>> response = controller.getDebtsForAuthenticatedUserByGroup(1);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertTrue(response.getBody().data().isEmpty());
    }

    @Test
    void testGetDebts_ValidationError() {
        SplitBillService mockService = Mockito.mock(SplitBillService.class);

        Mockito.when(mockService.getTransactionDebts(Mockito.anyInt()))
                .thenThrow(new IllegalArgumentException("Invalid group ID"));

        SplitBillController controller = new SplitBillController(mockService);

        ResponseEntity<CustomResponseBody<List<DebtResponse>>> response = controller.getDebts(-1);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("Invalid group ID", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testGetDebts_GenericException() {
        SplitBillService mockService = Mockito.mock(SplitBillService.class);

        Mockito.when(mockService.getTransactionDebts(Mockito.anyInt()))
                .thenThrow(new RuntimeException("Unexpected error occurred"));

        SplitBillController controller = new SplitBillController(mockService);

        ResponseEntity<CustomResponseBody<List<DebtResponse>>> response = controller.getDebts(1);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("An unexpected error occurred while fetching debts", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testGetDebts_EmptyList() {
        SplitBillService mockService = Mockito.mock(SplitBillService.class);

        Mockito.when(mockService.getTransactionDebts(Mockito.anyInt()))
                .thenReturn(List.of());

        SplitBillController controller = new SplitBillController(mockService);

        ResponseEntity<CustomResponseBody<List<DebtResponse>>> response = controller.getDebts(1);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertEquals("Debts fetched successfully", response.getBody().message());
        Assertions.assertTrue(response.getBody().data().isEmpty());
    }

    @Test
    void testGetSimplifiedDebt_Success() {
        // Arrange
        SplitBillService mockService = Mockito.mock(SplitBillService.class);

        List<DebtResponse> mockDebts = List.of(
                DebtResponse.builder()
                        .paidByEmail("payer1@example.com")
                        .owesToEmail("owed1@example.com")
                        .amount(100.0)
                        .build(),
                DebtResponse.builder()
                        .paidByEmail("payer2@example.com")
                        .owesToEmail("owed2@example.com")
                        .amount(150.0)
                        .build()
        );

        Mockito.when(mockService.getgroupdebtsummary(Mockito.anyInt())).thenReturn(mockDebts);

        SplitBillController controller = new SplitBillController(mockService);

        // Act
        ResponseEntity<CustomResponseBody<List<DebtResponse>>> response = controller.getSimplifiedDebt(1);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertEquals("Group summary fetched successfully", response.getBody().message());
        Assertions.assertEquals(2, response.getBody().data().size());

        DebtResponse debt1 = response.getBody().data().get(0);
        Assertions.assertEquals("payer1@example.com", debt1.getPaidByEmail());
        Assertions.assertEquals("owed1@example.com", debt1.getOwesToEmail());
        Assertions.assertEquals(100.0, debt1.getAmount());

        DebtResponse debt2 = response.getBody().data().get(1);
        Assertions.assertEquals("payer2@example.com", debt2.getPaidByEmail());
        Assertions.assertEquals("owed2@example.com", debt2.getOwesToEmail());
        Assertions.assertEquals(150.0, debt2.getAmount());
    }

    @Test
    void testGetSimplifiedDebt_ValidationError() {
        // Arrange
        SplitBillService mockService = Mockito.mock(SplitBillService.class);

        Mockito.when(mockService.getgroupdebtsummary(Mockito.anyInt()))
                .thenThrow(new IllegalArgumentException("Invalid group ID"));

        SplitBillController controller = new SplitBillController(mockService);

        // Act
        ResponseEntity<CustomResponseBody<List<DebtResponse>>> response = controller.getSimplifiedDebt(-1);

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("Invalid group ID", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testGetSimplifiedDebt_UnexpectedError() {
        // Arrange
        SplitBillService mockService = Mockito.mock(SplitBillService.class);

        Mockito.when(mockService.getgroupdebtsummary(Mockito.anyInt()))
                .thenThrow(new RuntimeException("Unexpected error occurred"));

        SplitBillController controller = new SplitBillController(mockService);

        // Act
        ResponseEntity<CustomResponseBody<List<DebtResponse>>> response = controller.getSimplifiedDebt(1);

        // Assert
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("An unexpected error occurred while fetching the group summary", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testGetSimplifiedDebt_EmptyList() {
        // Arrange
        SplitBillService mockService = Mockito.mock(SplitBillService.class);

        Mockito.when(mockService.getgroupdebtsummary(Mockito.anyInt())).thenReturn(List.of());

        SplitBillController controller = new SplitBillController(mockService);

        // Act
        ResponseEntity<CustomResponseBody<List<DebtResponse>>> response = controller.getSimplifiedDebt(1);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertEquals("Group summary fetched successfully", response.getBody().message());
        Assertions.assertTrue(response.getBody().data().isEmpty());
    }

    @Test
    void testGetDebitCreditSummary_Success() {
        // Arrange
        SplitBillService mockService = Mockito.mock(SplitBillService.class);

        DebitCreditSummaryResponse mockSummary = DebitCreditSummaryResponse.builder()
                .totalDebit(150.0)
                .totalCredit(200.0)
                .build();

        Mockito.when(mockService.getDebitCreditSummary(Mockito.anyInt())).thenReturn(mockSummary);

        SplitBillController controller = new SplitBillController(mockService);

        // Act
        ResponseEntity<CustomResponseBody<DebitCreditSummaryResponse>> response = controller.getDebitCreditSummary(1);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertEquals("Fetched debit-credit summary successfully", response.getBody().message());

        DebitCreditSummaryResponse responseData = response.getBody().data();
        Assertions.assertNotNull(responseData);
        Assertions.assertEquals(150.0, responseData.getTotalDebit());
        Assertions.assertEquals(200.0, responseData.getTotalCredit());
        //Assertions.assertEquals(50.0, responseData.getNetBalance());
    }


    @Test
    void testGetDebitCreditSummary_UnexpectedError() {
        // Arrange
        SplitBillService mockService = Mockito.mock(SplitBillService.class);

        Mockito.when(mockService.getDebitCreditSummary(Mockito.anyInt()))
                .thenThrow(new RuntimeException("Unexpected error occurred"));

        SplitBillController controller = new SplitBillController(mockService);

        // Act
        ResponseEntity<CustomResponseBody<DebitCreditSummaryResponse>> response = controller.getDebitCreditSummary(1);

        // Assert
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("Failed to fetch debit-credit summary", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testGetDebitCreditSummary_EmptySummary() {
        // Arrange
        SplitBillService mockService = Mockito.mock(SplitBillService.class);

        DebitCreditSummaryResponse emptySummary = DebitCreditSummaryResponse.builder()
                .totalDebit(0.0)
                .totalCredit(0.0)
                .build();

        Mockito.when(mockService.getDebitCreditSummary(Mockito.anyInt())).thenReturn(emptySummary);

        SplitBillController controller = new SplitBillController(mockService);

        // Act
        ResponseEntity<CustomResponseBody<DebitCreditSummaryResponse>> response = controller.getDebitCreditSummary(1);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertEquals("Fetched debit-credit summary successfully", response.getBody().message());

        DebitCreditSummaryResponse responseData = response.getBody().data();
        Assertions.assertNotNull(responseData);
        Assertions.assertEquals(0.0, responseData.getTotalDebit());
        Assertions.assertEquals(0.0, responseData.getTotalCredit());
    }
}

