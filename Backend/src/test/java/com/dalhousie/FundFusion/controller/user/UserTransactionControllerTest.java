package com.dalhousie.FundFusion.controller.user;

import com.dalhousie.FundFusion.dto.DateRangeEntity;
import com.dalhousie.FundFusion.user.controller.UserTransactionController;
import com.dalhousie.FundFusion.user.requestEntity.UserTransactionRequest;
import com.dalhousie.FundFusion.user.responseEntity.UserTransactionResponse;
import com.dalhousie.FundFusion.user.service.UserTransactionService;
import com.dalhousie.FundFusion.util.CustomResponseBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

public class UserTransactionControllerTest {

    @Test
    void testLogTransaction_Success() {
        UserTransactionService mockService = Mockito.mock(UserTransactionService.class);

        UserTransactionResponse mockResponse = UserTransactionResponse.builder()
                .txnId(1)
                .txnDesc("Lunch")
                .expense(100.0f)
                .txnDate(LocalDate.now())
                .category("Food")
                .build();

        Mockito.when(mockService.logTransaction(Mockito.any(UserTransactionRequest.class)))
                .thenReturn(mockResponse);

        UserTransactionController controller = new UserTransactionController(mockService);

        UserTransactionRequest request = new UserTransactionRequest();
        request.setCategoryId(1);

        ResponseEntity<CustomResponseBody<UserTransactionResponse>> response = controller.logTransaction(request);

        Assertions.assertEquals(201, response.getStatusCodeValue());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertEquals("Transaction logged successfully", response.getBody().message());
        Assertions.assertNotNull(response.getBody().data());

        UserTransactionResponse responseData = response.getBody().data();
        Assertions.assertEquals(1, responseData.getTxnId());
        Assertions.assertEquals("Lunch", responseData.getTxnDesc());
        Assertions.assertEquals(100.0f, responseData.getExpense());
        Assertions.assertEquals("Food", responseData.getCategory());
        Assertions.assertEquals(LocalDate.now(), responseData.getTxnDate());
    }

    @Test
    void testLogTransaction_Failure() {
        UserTransactionService mockService = Mockito.mock(UserTransactionService.class);

        Mockito.when(mockService.logTransaction(Mockito.any(UserTransactionRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        UserTransactionController controller = new UserTransactionController(mockService);

        UserTransactionRequest request = new UserTransactionRequest();
        request.setCategoryId(1);

        ResponseEntity<CustomResponseBody<UserTransactionResponse>> response = controller.logTransaction(request);

        Assertions.assertEquals(400, response.getStatusCodeValue());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("Something went wrong", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testGetAllTransactions_Success() {
        UserTransactionService mockService = Mockito.mock(UserTransactionService.class);

        List<UserTransactionResponse> mockTransactions = List.of(
                UserTransactionResponse.builder()
                        .txnId(1)
                        .txnDesc("Lunch")
                        .expense(100.0f)
                        .txnDate(LocalDate.now())
                        .category("Food")
                        .build(),
                UserTransactionResponse.builder()
                        .txnId(2)
                        .txnDesc("Groceries")
                        .expense(50.0f)
                        .txnDate(LocalDate.now())
                        .category("Essentials")
                        .build()
        );

        Mockito.when(mockService.getAllTransactions()).thenReturn(mockTransactions);

        UserTransactionController controller = new UserTransactionController(mockService);

        ResponseEntity<CustomResponseBody<List<UserTransactionResponse>>> response = controller.getAllTransactions();

        Assertions.assertEquals(201, response.getStatusCodeValue());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertEquals("All transactions fetched successfully", response.getBody().message());
        Assertions.assertNotNull(response.getBody().data());
        Assertions.assertEquals(2, response.getBody().data().size());

        UserTransactionResponse txn1 = response.getBody().data().get(0);
        Assertions.assertEquals(1, txn1.getTxnId());
        Assertions.assertEquals("Lunch", txn1.getTxnDesc());
        Assertions.assertEquals(100.0f, txn1.getExpense());
        Assertions.assertEquals("Food", txn1.getCategory());

        UserTransactionResponse txn2 = response.getBody().data().get(1);
        Assertions.assertEquals(2, txn2.getTxnId());
        Assertions.assertEquals("Groceries", txn2.getTxnDesc());
        Assertions.assertEquals(50.0f, txn2.getExpense());
        Assertions.assertEquals("Essentials", txn2.getCategory());
    }

    @Test
    void testDeleteTransaction_NotFound() {
        UserTransactionService mockService = Mockito.mock(UserTransactionService.class);

        Mockito.doThrow(new NoSuchElementException("Transaction not found"))
                .when(mockService).deleteTransaction(Mockito.anyInt());

        UserTransactionController controller = new UserTransactionController(mockService);

        ResponseEntity<CustomResponseBody<UserTransactionResponse>> response = controller.deleteTransaction(999);

        Assertions.assertEquals(400, response.getStatusCodeValue());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("Transaction not found", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testGetTransactionsBetweenDate_Success() {
        UserTransactionService mockService = Mockito.mock(UserTransactionService.class);

        List<UserTransactionResponse> mockTransactions = List.of(
                UserTransactionResponse.builder()
                        .txnId(1)
                        .txnDesc("Lunch")
                        .expense(100.0f)
                        .txnDate(LocalDate.of(2024, 11, 1))
                        .category("Food")
                        .build(),
                UserTransactionResponse.builder()
                        .txnId(2)
                        .txnDesc("Groceries")
                        .expense(50.0f)
                        .txnDate(LocalDate.of(2024, 11, 2))
                        .category("Essentials")
                        .build()
        );

        Mockito.when(mockService.getTransactionsWithinDateRange(Mockito.any(DateRangeEntity.class)))
                .thenReturn(mockTransactions);

        UserTransactionController controller = new UserTransactionController(mockService);

        ResponseEntity<CustomResponseBody<List<UserTransactionResponse>>> response =
                controller.getTransactionsBetweenDate("2024-11-01", "2024-11-03");

        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertEquals("Transaction within daterange fetched successfully", response.getBody().message());
        Assertions.assertNotNull(response.getBody().data());
        Assertions.assertEquals(2, response.getBody().data().size());

        UserTransactionResponse txn1 = response.getBody().data().get(0);
        Assertions.assertEquals(1, txn1.getTxnId());
        Assertions.assertEquals("Lunch", txn1.getTxnDesc());
        Assertions.assertEquals(100.0f, txn1.getExpense());
        Assertions.assertEquals("Food", txn1.getCategory());
        Assertions.assertEquals(LocalDate.of(2024, 11, 1), txn1.getTxnDate());

        UserTransactionResponse txn2 = response.getBody().data().get(1);
        Assertions.assertEquals(2, txn2.getTxnId());
        Assertions.assertEquals("Groceries", txn2.getTxnDesc());
        Assertions.assertEquals(50.0f, txn2.getExpense());
        Assertions.assertEquals("Essentials", txn2.getCategory());
        Assertions.assertEquals(LocalDate.of(2024, 11, 2), txn2.getTxnDate());
    }

    @Test
    void testGetTransactionsWithCategory_Success() {
        UserTransactionService mockService = Mockito.mock(UserTransactionService.class);

        List<UserTransactionResponse> mockTransactions = List.of(
                UserTransactionResponse.builder()
                        .txnId(1)
                        .txnDesc("Lunch")
                        .expense(100.0f)
                        .txnDate(LocalDate.now())
                        .category("Food")
                        .build(),
                UserTransactionResponse.builder()
                        .txnId(2)
                        .txnDesc("Snacks")
                        .expense(30.0f)
                        .txnDate(LocalDate.now())
                        .category("Food")
                        .build()
        );

        Mockito.when(mockService.getTransactionsWithCategory(Mockito.any(UserTransactionRequest.class)))
                .thenReturn(mockTransactions);

        UserTransactionController controller = new UserTransactionController(mockService);

        ResponseEntity<CustomResponseBody<List<UserTransactionResponse>>> response =
                controller.getTransactionsWithCategory(1);

        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertEquals("Transaction with category fetched successfully", response.getBody().message());
        Assertions.assertNotNull(response.getBody().data());
        Assertions.assertEquals(2, response.getBody().data().size());

        UserTransactionResponse txn1 = response.getBody().data().get(0);
        Assertions.assertEquals(1, txn1.getTxnId());
        Assertions.assertEquals("Lunch", txn1.getTxnDesc());
        Assertions.assertEquals(100.0f, txn1.getExpense());
        Assertions.assertEquals("Food", txn1.getCategory());
        Assertions.assertEquals(LocalDate.now(), txn1.getTxnDate());

        UserTransactionResponse txn2 = response.getBody().data().get(1);
        Assertions.assertEquals(2, txn2.getTxnId());
        Assertions.assertEquals("Snacks", txn2.getTxnDesc());
        Assertions.assertEquals(30.0f, txn2.getExpense());
        Assertions.assertEquals("Food", txn2.getCategory());
        Assertions.assertEquals(LocalDate.now(), txn2.getTxnDate());
    }

    @Test
    void testUpdateTransaction_Success() {
        UserTransactionService mockService = Mockito.mock(UserTransactionService.class);

        UserTransactionResponse mockResponse = UserTransactionResponse.builder()
                .txnId(1)
                .txnDesc("Updated Lunch")
                .expense(120.0f)
                .txnDate(LocalDate.now())
                .category("Food")
                .build();

        Mockito.when(mockService.updateTransaction(Mockito.any(UserTransactionRequest.class)))
                .thenReturn(mockResponse);

        UserTransactionController controller = new UserTransactionController(mockService);

        UserTransactionRequest request = new UserTransactionRequest();
        request.setTxnId(1);
        request.setCategoryId(1);

        ResponseEntity<CustomResponseBody<UserTransactionResponse>> response = controller.updateTransaction(request);

        Assertions.assertEquals(201, response.getStatusCodeValue());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertEquals("Transaction updated successfully", response.getBody().message());
        Assertions.assertNotNull(response.getBody().data());
        Assertions.assertEquals("Updated Lunch", response.getBody().data().getTxnDesc());
        Assertions.assertEquals(120.0f, response.getBody().data().getExpense());
    }

    @Test
    void testUpdateTransaction_Failure() {
        UserTransactionService mockService = Mockito.mock(UserTransactionService.class);

        Mockito.when(mockService.updateTransaction(Mockito.any(UserTransactionRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        UserTransactionController controller = new UserTransactionController(mockService);

        UserTransactionRequest request = new UserTransactionRequest();
        request.setTxnId(1);
        request.setCategoryId(1);

        ResponseEntity<CustomResponseBody<UserTransactionResponse>> response = controller.updateTransaction(request);

        Assertions.assertEquals(400, response.getStatusCodeValue());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("Something went wrong", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testDeleteTransaction_Success() {
        UserTransactionService mockService = Mockito.mock(UserTransactionService.class);

        Mockito.doNothing().when(mockService).deleteTransaction(Mockito.anyInt());

        UserTransactionController controller = new UserTransactionController(mockService);

        ResponseEntity<CustomResponseBody<UserTransactionResponse>> response = controller.deleteTransaction(1);

        Assertions.assertEquals(201, response.getStatusCodeValue());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertEquals("Transaction deleted successfully", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testGetTransactionsBetweenDate_Failure() {
        UserTransactionService mockService = Mockito.mock(UserTransactionService.class);

        Mockito.when(mockService.getTransactionsWithinDateRange(Mockito.any(DateRangeEntity.class)))
                .thenThrow(new NoSuchElementException("No transactions found"));

        UserTransactionController controller = new UserTransactionController(mockService);

        ResponseEntity<CustomResponseBody<List<UserTransactionResponse>>> response =
                controller.getTransactionsBetweenDate("2024-11-01", "2024-11-03");

        Assertions.assertEquals(400, response.getStatusCodeValue());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("Something went wrong", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testGetTransactionsWithCategory_Failure() {
        UserTransactionService mockService = Mockito.mock(UserTransactionService.class);

        Mockito.when(mockService.getTransactionsWithCategory(Mockito.any(UserTransactionRequest.class)))
                .thenThrow(new NoSuchElementException("No transactions found"));

        UserTransactionController controller = new UserTransactionController(mockService);

        ResponseEntity<CustomResponseBody<List<UserTransactionResponse>>> response =
                controller.getTransactionsWithCategory(1);

        Assertions.assertEquals(400, response.getStatusCodeValue());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("Something went wrong", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }
}

