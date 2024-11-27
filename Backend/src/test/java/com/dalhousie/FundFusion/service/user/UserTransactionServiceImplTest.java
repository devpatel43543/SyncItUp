package com.dalhousie.FundFusion.service.user;

import com.dalhousie.FundFusion.category.entity.Category;
import com.dalhousie.FundFusion.category.requestEntity.CategoryRequest;
import com.dalhousie.FundFusion.category.service.CategoryService;
import com.dalhousie.FundFusion.dto.DateRangeEntity;
import com.dalhousie.FundFusion.user.entity.User;
import com.dalhousie.FundFusion.user.entity.UserTransaction;
import com.dalhousie.FundFusion.user.repository.UserTransactionRepository;
import com.dalhousie.FundFusion.user.requestEntity.UserTransactionRequest;
import com.dalhousie.FundFusion.user.responseEntity.UserTransactionResponse;
import com.dalhousie.FundFusion.user.service.UserService;
import com.dalhousie.FundFusion.user.service.UserTransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserTransactionServiceImplTest {

    // Constants for magic numbers
    private static final float DEFAULT_EXPENSE_LUNCH = 100f;
    private static final float UPDATED_EXPENSE = 300f;
    private static final float DEFAULT_EXPENSE_GROCERIES = 50f;
    private static final float TRANSACTION_DATE_RANGE = 5f;
    private static final float DINNER_EXPENSE = 30f;
    private static final float MOVIE_EXPENSE = 15f;

    @Mock
    private UserTransactionRepository userTransactionRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserTransactionServiceImpl userTransactionService;

    @BeforeEach
    void setUp() {
        // Mock the Security Context before each test
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void logTransaction_ShouldSaveTransaction() {

        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setName("Test User");
        mockUser.setEmail("testuser@example.com");
        mockUser.setPassword("password123");
        mockUser.setEmailVerified(true);

        when(userService.getCurrentUser()).thenReturn(mockUser);

        Authentication authentication = mock(Authentication.class);
        lenient().when(authentication.getPrincipal()).thenReturn(mockUser);
        lenient().when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(authentication);

        Category mockCategory = new Category();
        mockCategory.setCategoryName("Food");
        when(categoryService.getCategory(any(CategoryRequest.class))).thenReturn(mockCategory);

        UserTransactionRequest request = new UserTransactionRequest();
        request.setExpense(DEFAULT_EXPENSE_LUNCH);
        request.setCategoryId(1);
        request.setTxnDesc("Lunch");
        request.setTxnDate(LocalDate.now());

        UserTransaction mockTransaction = UserTransaction.builder()
                .txnId(1)
                .user(mockUser)
                .expense(DEFAULT_EXPENSE_LUNCH)
                .category(mockCategory)
                .txnDesc("Lunch")
                .txnDate(LocalDate.now())
                .build();

        when(userTransactionRepository.save(any(UserTransaction.class))).thenReturn(mockTransaction);

        UserTransactionResponse response = userTransactionService.logTransaction(request);

        assertEquals(1, response.getTxnId());
        assertEquals("Lunch", response.getTxnDesc());
        assertEquals(DEFAULT_EXPENSE_LUNCH, response.getExpense());
        assertEquals("Food", response.getCategory());
    }

    @Test
    void updateTransaction_ShouldUpdateExistingTransaction() {

        UserTransaction existingTransaction = new UserTransaction();
        existingTransaction.setTxnId(1);
        existingTransaction.setTxnDesc("Old Description");
        existingTransaction.setExpense(DEFAULT_EXPENSE_GROCERIES);

        Category mockCategory = new Category();
        mockCategory.setCategoryId(1);  // Set categoryId
        mockCategory.setCategoryName("Food");  // Set categoryName

        existingTransaction.setCategory(mockCategory);

        when(userTransactionRepository.findById(1)).thenReturn(Optional.of(existingTransaction));

        when(categoryService.getCategory(any(CategoryRequest.class))).thenReturn(mockCategory);

        when(userTransactionRepository.save(any(UserTransaction.class))).thenAnswer(invocation -> {
            UserTransaction updatedTransaction = invocation.getArgument(0);
            existingTransaction.setTxnDesc(updatedTransaction.getTxnDesc());
            existingTransaction.setExpense(updatedTransaction.getExpense());
            existingTransaction.setCategory(updatedTransaction.getCategory());
            return existingTransaction;
        });


        UserTransactionRequest request = new UserTransactionRequest();
        request.setTxnId(1);
        request.setExpense(UPDATED_EXPENSE);
        request.setTxnDesc("Updated Description");
        request.setCategoryId(1);

        // Act
        UserTransactionResponse response = userTransactionService.updateTransaction(request);

        // Assert
        // Ensure the transaction was updated
        assertEquals(1, response.getTxnId());
        assertEquals("Updated Description", response.getTxnDesc());
        assertEquals(UPDATED_EXPENSE, response.getExpense());
        assertEquals("Food", response.getCategory());
    }


    @Test
    void getAllTransactions_ShouldReturnUserTransactions() {

        User mockUser = new User();
        when(userService.getCurrentUser()).thenReturn(mockUser);

        UserTransaction transaction = new UserTransaction();
        transaction.setTxnId(1);
        transaction.setTxnDesc("Groceries");
        transaction.setExpense(DEFAULT_EXPENSE_GROCERIES);
        transaction.setTxnDate(LocalDate.now());
        Category category = new Category();
        category.setCategoryName("Food");
        transaction.setCategory(category);

        when(userTransactionRepository.findByUser(mockUser)).thenReturn(List.of(transaction));

        List<UserTransactionResponse> transactions = userTransactionService.getAllTransactions();

        assertEquals(1, transactions.size());
        assertEquals("Groceries", transactions.get(0).getTxnDesc());
    }

    @Test
    void getTransactionsWithinDateRange_ShouldReturnTransactionsInDateRange() {

        DateRangeEntity dateRange = new DateRangeEntity();
        dateRange.setFromDate(LocalDate.now().minusDays((long) TRANSACTION_DATE_RANGE));
        dateRange.setToDate(LocalDate.now());

        User mockUser = new User();
        when(userService.getCurrentUser()).thenReturn(mockUser);

        UserTransaction transaction = new UserTransaction();
        transaction.setTxnId(1);
        transaction.setTxnDate(LocalDate.now());
        transaction.setTxnDesc("Dinner");
        transaction.setExpense(DINNER_EXPENSE);
        Category category = new Category();
        category.setCategoryName("Food");
        transaction.setCategory(category);

        // Breaking the long statement into separate variables
        LocalDate fromDate = dateRange.getFromDate();
        LocalDate toDate = dateRange.getToDate();
        when(userTransactionRepository.findByUserAndTxnDateBetween(mockUser, fromDate, toDate))
                .thenReturn(List.of(transaction));

        List<UserTransactionResponse> transactions = userTransactionService.getTransactionsWithinDateRange(dateRange);

        assertEquals(1, transactions.size());
        assertEquals("Dinner", transactions.get(0).getTxnDesc());
    }

    @Test
    void getTransactionsWithCategory_ShouldReturnTransactionsWithGivenCategory() {

        User mockUser = new User();
        Category category = new Category();
        category.setCategoryName("Entertainment");

        UserTransaction transaction = new UserTransaction();
        transaction.setTxnId(1);
        transaction.setTxnDesc("Movie");
        transaction.setExpense(MOVIE_EXPENSE);
        transaction.setTxnDate(LocalDate.now());
        transaction.setCategory(category);

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(categoryService.getCategory(any(CategoryRequest.class))).thenReturn(category);
        when(userTransactionRepository.findByUserAndCategory(mockUser, category)).thenReturn(List.of(transaction));

        UserTransactionRequest request = new UserTransactionRequest();
        request.setCategoryId(1);

        List<UserTransactionResponse> transactions = userTransactionService.getTransactionsWithCategory(request);

        assertEquals(1, transactions.size());
        assertEquals("Movie", transactions.get(0).getTxnDesc());
    }

    @Test
    void deleteTransaction_ShouldDeleteTransactionById() {

        int txnId = 1;
        UserTransaction transaction = new UserTransaction();
        transaction.setTxnId(txnId);

        when(userTransactionRepository.findById(txnId)).thenReturn(Optional.of(transaction));
        doNothing().when(userTransactionRepository).deleteById(txnId);

        userTransactionService.deleteTransaction(txnId);

        verify(userTransactionRepository, times(1)).deleteById(txnId);
    }

    @Test
    void checkDate_ShouldSetTxnDateIfNull() {

        UserTransaction transaction = new UserTransaction();
        transaction.setTxnDate(null);

        userTransactionService.checkDate(transaction);

        assertNotNull(transaction.getTxnDate());
        assertEquals(LocalDate.now(), transaction.getTxnDate());
    }
}
