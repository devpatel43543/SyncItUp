package com.dalhousie.FundFusion.service.splitBill;

import com.dalhousie.FundFusion.group.entity.Group;
import com.dalhousie.FundFusion.group.repository.GroupRepository;
import com.dalhousie.FundFusion.splitBill.entity.ExpenseShare;
import com.dalhousie.FundFusion.splitBill.entity.GroupPartnership;
import com.dalhousie.FundFusion.splitBill.entity.Transaction;
import com.dalhousie.FundFusion.splitBill.repository.ExpenseShareRepository;
import com.dalhousie.FundFusion.splitBill.repository.GroupPartnershipRepository;
import com.dalhousie.FundFusion.splitBill.repository.TransactionRepository;
import com.dalhousie.FundFusion.splitBill.requestEntity.*;
import com.dalhousie.FundFusion.splitBill.responseEntity.*;
import com.dalhousie.FundFusion.splitBill.service.SplitBillServiceImpl;
import com.dalhousie.FundFusion.user.entity.User;
import com.dalhousie.FundFusion.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SplitBillServiceImplTest {

    @InjectMocks
    private SplitBillServiceImpl splitBillService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ExpenseShareRepository expenseShareRepository;

    @Mock
    private GroupPartnershipRepository groupPartnershipRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private Group testGroup;
    private User testUser1;
    private User testUser2;
    private Transaction testTransaction;

    // Constants for magic numbers
    private static final double INITIAL_AMOUNT = 100.0;
    private static final double UPDATED_AMOUNT = 200.0;
    private static final double SHARE_AMOUNT = 50.0;
    private static final double DEBIT_CREDIT_AMOUNT = 30.0;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testGroup = createTestGroup();
        testUser1 = createTestUser("user1@example.com");
        testUser2 = createTestUser("user2@example.com");
        testTransaction = createTestTransaction(testGroup, testUser1, INITIAL_AMOUNT);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    private Group createTestGroup() {
        Group group = new Group();
        group.setId(1);
        group.setGroupName("Test Group");
        return group;
    }

    private User createTestUser(String email) {
        User user = new User();
        user.setEmail(email);
        return user;
    }

    private Transaction createTestTransaction(Group group, User paidBy, double amount) {
        return Transaction.builder()
                .id(1L)
                .group(group)
                .paidBy(paidBy)
                .amount(amount)
                .title("Lunch")
                .build();
    }

    private ExpenseShare createExpenseShare(Transaction transaction, User user, double shareAmount) {
        return ExpenseShare.builder()
                .transaction(transaction)
                .user(user)
                .shareAmount(shareAmount)
                .build();
    }

    private AddExpenseRequest createAddExpenseRequest() {
        AddExpenseRequest request = new AddExpenseRequest();
        request.setGroupId(1);
        request.setPaidByEmail("user1@example.com");
        request.setAmount(INITIAL_AMOUNT);
        request.setTitle("Lunch");
        request.setInvolvedMembers(Arrays.asList("user1@example.com", "user2@example.com"));
        return request;
    }

    private SettleDebtRequest createSettleDebtRequest() {
        SettleDebtRequest request = new SettleDebtRequest();
        request.setGroupId(1);
        request.setCreditorEmail("user1@example.com");
        request.setAmount(SHARE_AMOUNT);
        return request;
    }

    @Test
    void testCreateExpense_Success() {
        AddExpenseRequest request = createAddExpenseRequest();

        when(groupRepository.findById(1)).thenReturn(Optional.of(testGroup));
        when(userRepository.findByEmail("user1@example.com")).thenReturn(Optional.of(testUser1));
        when(userRepository.findByEmail("user2@example.com")).thenReturn(Optional.of(testUser2));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        AddExpenseResponse response = splitBillService.createExpense(request);

        assertNotNull(response);
        assertEquals("Test Group", response.getGroupName());
        assertEquals(INITIAL_AMOUNT, response.getTotalAmount());
    }

    @Test
    void testUpdateExpense_Success() {
        UpdateExpenseRequest request = new UpdateExpenseRequest();
        request.setTransactionId(1L);
        request.setAmount(UPDATED_AMOUNT);
        request.setInvolvedMembers(Arrays.asList("user1@example.com", "user2@example.com"));

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));
        when(expenseShareRepository.findByTransaction(testTransaction)).thenReturn(Collections.emptyList());
        when(userRepository.findByEmail("user1@example.com")).thenReturn(Optional.of(testUser1));
        when(userRepository.findByEmail("user2@example.com")).thenReturn(Optional.of(testUser2));

        AddExpenseResponse response = splitBillService.updateExpense(request);

        assertNotNull(response);
        assertEquals(UPDATED_AMOUNT, response.getTotalAmount());
    }

    @Test
    void testDeleteExpense_Success() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));
        when(expenseShareRepository.findByTransaction(testTransaction)).thenReturn(Collections.emptyList());

        splitBillService.deleteExpense(1L);

        verify(transactionRepository, times(1)).delete(testTransaction);
        verify(expenseShareRepository, times(1)).deleteAll(Collections.emptyList());
    }

    @Test
    void testGetTransactionDebts() {
        ExpenseShare expenseShare = createExpenseShare(testTransaction, testUser2, SHARE_AMOUNT);

        when(groupRepository.findById(1)).thenReturn(Optional.of(testGroup));
        when(expenseShareRepository.findByTransactionGroup(testGroup))
                .thenReturn(Collections.singletonList(expenseShare));

        List<DebtResponse> debts = splitBillService.getTransactionDebts(1);

        assertNotNull(debts);
        assertEquals(1, debts.size());
        assertEquals("user1@example.com", debts.get(0).getPaidByEmail());
    }

    @Test
    void testGetGroupDebtSummary() {
        GroupPartnership groupPartnership = GroupPartnership.builder()
                .group(testGroup)
                .creditor(testUser1)
                .debtor(testUser2)
                .amount(INITIAL_AMOUNT)
                .build();

        when(authentication.getName()).thenReturn("user1@example.com");
        when(userRepository.findByEmail("user1@example.com")).thenReturn(Optional.of(testUser1));
        when(groupRepository.findById(1)).thenReturn(Optional.of(testGroup));
        when(groupPartnershipRepository.findByGroup(testGroup))
                .thenReturn(Collections.singletonList(groupPartnership));

        List<DebtResponse> summary = splitBillService.getgroupdebtsummary(1);

        assertNotNull(summary);
        assertEquals(1, summary.size());
        assertEquals(INITIAL_AMOUNT, summary.get(0).getAmount());
    }

    @Test
    void testGetTransactionSummary() {
        when(groupRepository.findById(1)).thenReturn(Optional.of(testGroup));
        when(transactionRepository.findByGroup(testGroup)).thenReturn(Collections.singletonList(testTransaction));

        List<TransactionSummaryResponse> summary = splitBillService.getTransactionSummary(1);

        assertNotNull(summary);
        assertEquals(1, summary.size());
        assertEquals(INITIAL_AMOUNT, summary.get(0).getAmountPaid());
    }

    @Test
    void testGetDebtsForAuthenticatedUserByGroup() {
        GroupPartnership groupPartnership = GroupPartnership.builder()
                .group(testGroup)
                .creditor(testUser1)
                .debtor(testUser2)
                .amount(SHARE_AMOUNT)
                .build();

        List<GroupPartnership> groupPartnerships = Collections.singletonList(groupPartnership); // Extracted into a variable

        when(authentication.getName()).thenReturn("user2@example.com");
        when(userRepository.findByEmail("user2@example.com")).thenReturn(Optional.of(testUser2));
        when(groupRepository.findById(1)).thenReturn(Optional.of(testGroup));
        when(groupPartnershipRepository.findByGroupAndDebtor(testGroup, testUser2))
                .thenReturn(groupPartnerships);

        List<UserDebtResponse> debts = splitBillService.getDebtsForAuthenticatedUserByGroup(1);

        assertNotNull(debts);
        assertEquals(1, debts.size());
        assertEquals(SHARE_AMOUNT, debts.get(0).getAmount());
    }

    @Test
    void testSettleDebt_Success() {
        SettleDebtRequest request = createSettleDebtRequest();

        GroupPartnership groupPartnership = GroupPartnership.builder()
                .group(testGroup)
                .creditor(testUser1)
                .debtor(testUser2)
                .amount(SHARE_AMOUNT)
                .build();

        when(authentication.getName()).thenReturn("user2@example.com");
        when(userRepository.findByEmail("user2@example.com")).thenReturn(Optional.of(testUser2));
        when(userRepository.findByEmail("user1@example.com")).thenReturn(Optional.of(testUser1));
        when(groupRepository.findById(1)).thenReturn(Optional.of(testGroup));
        when(groupPartnershipRepository.findByGroupAndCreditorAndDebtor(testGroup, testUser1, testUser2))
                .thenReturn(Optional.of(groupPartnership));

        SettlementResponse response = splitBillService.settleDebt(request);

        assertNotNull(response);
        assertEquals(SHARE_AMOUNT, response.getAmountSettled());
    }

    @Test
    void testGetDebitCreditSummary() {

        GroupPartnership groupPartnership = GroupPartnership.builder()
                .group(testGroup)
                .creditor(testUser1)
                .debtor(testUser2)
                .amount(DEBIT_CREDIT_AMOUNT)
                .build();

        List<GroupPartnership> groupPartnerships = Collections.singletonList(groupPartnership); // Intermediate variable

        when(authentication.getName()).thenReturn("user2@example.com");
        when(userRepository.findByEmail("user2@example.com")).thenReturn(Optional.of(testUser2));
        when(groupRepository.findById(1)).thenReturn(Optional.of(testGroup));
        when(groupPartnershipRepository.findByGroupAndDebtor(testGroup, testUser2))
                .thenReturn(groupPartnerships); // Using the variable here

        DebitCreditSummaryResponse summary = splitBillService.getDebitCreditSummary(1);

        assertNotNull(summary);
        assertEquals(DEBIT_CREDIT_AMOUNT, summary.getTotalDebit());
        assertEquals(0.0, summary.getTotalCredit());
    }
}
