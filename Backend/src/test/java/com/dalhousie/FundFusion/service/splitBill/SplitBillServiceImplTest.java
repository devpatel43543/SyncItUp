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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testGroup = new Group();
        testGroup.setId(1);
        testGroup.setGroupName("Test Group");

        testUser1 = new User();
        testUser1.setEmail("user1@example.com");

        testUser2 = new User();
        testUser2.setEmail("user2@example.com");

        testTransaction = Transaction.builder()
                .id(1L)
                .group(testGroup)
                .paidBy(testUser1)
                .amount(100.0)
                .title("Lunch")
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testCreateExpense_Success() {
        AddExpenseRequest request = new AddExpenseRequest();
        request.setGroupId(1);
        request.setPaidByEmail("user1@example.com");
        request.setAmount(100.0);
        request.setTitle("Lunch");
        request.setInvolvedMembers(Arrays.asList("user1@example.com", "user2@example.com"));

        when(groupRepository.findById(1)).thenReturn(Optional.of(testGroup));
        when(userRepository.findByEmail("user1@example.com")).thenReturn(Optional.of(testUser1));
        when(userRepository.findByEmail("user2@example.com")).thenReturn(Optional.of(testUser2));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        AddExpenseResponse response = splitBillService.createExpense(request);

        assertNotNull(response);
        assertEquals("Test Group", response.getGroupName());
        assertEquals(100.0, response.getTotalAmount());
    }

    @Test
    void testUpdateExpense_Success() {
        UpdateExpenseRequest request = new UpdateExpenseRequest();
        request.setTransactionId(1L);
        request.setAmount(200.0);
        request.setInvolvedMembers(Arrays.asList("user1@example.com", "user2@example.com"));

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));
        when(expenseShareRepository.findByTransaction(testTransaction)).thenReturn(Collections.emptyList());
        when(userRepository.findByEmail("user1@example.com")).thenReturn(Optional.of(testUser1));
        when(userRepository.findByEmail("user2@example.com")).thenReturn(Optional.of(testUser2));

        AddExpenseResponse response = splitBillService.updateExpense(request);

        assertNotNull(response);
        assertEquals(200.0, response.getTotalAmount());
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
        when(groupRepository.findById(1)).thenReturn(Optional.of(testGroup));
        when(expenseShareRepository.findByTransactionGroup(testGroup))
                .thenReturn(Collections.singletonList(ExpenseShare.builder()
                        .transaction(testTransaction)
                        .user(testUser2)
                        .shareAmount(50.0)
                        .build()));

        List<DebtResponse> debts = splitBillService.getTransactionDebts(1);

        assertNotNull(debts);
        assertEquals(1, debts.size());
        assertEquals("user1@example.com", debts.get(0).getPaidByEmail());
    }

    @Test
    void testGetGroupDebtSummary() {
        when(authentication.getName()).thenReturn("user1@example.com");
        when(userRepository.findByEmail("user1@example.com")).thenReturn(Optional.of(testUser1));
        when(groupRepository.findById(1)).thenReturn(Optional.of(testGroup));
        when(groupPartnershipRepository.findByGroup(testGroup)).thenReturn(Collections.singletonList(GroupPartnership.builder()
                .group(testGroup)
                .creditor(testUser1)
                .debtor(testUser2)
                .amount(100.0)
                .build()));

        List<DebtResponse> summary = splitBillService.getgroupdebtsummary(1);

        assertNotNull(summary);
        assertEquals(1, summary.size());
        assertEquals(100.0, summary.get(0).getAmount());
    }

    @Test
    void testGetTransactionSummary() {
        when(groupRepository.findById(1)).thenReturn(Optional.of(testGroup));
        when(transactionRepository.findByGroup(testGroup)).thenReturn(Collections.singletonList(testTransaction));

        List<TransactionSummaryResponse> summary = splitBillService.getTransactionSummary(1);

        assertNotNull(summary);
        assertEquals(1, summary.size());
        assertEquals(100.0, summary.get(0).getAmountPaid());
    }

    @Test
    void testGetDebtsForAuthenticatedUserByGroup() {
        when(authentication.getName()).thenReturn("user2@example.com");
        when(userRepository.findByEmail("user2@example.com")).thenReturn(Optional.of(testUser2));
        when(groupRepository.findById(1)).thenReturn(Optional.of(testGroup));
        when(groupPartnershipRepository.findByGroupAndDebtor(testGroup, testUser2))
                .thenReturn(Collections.singletonList(GroupPartnership.builder()
                        .group(testGroup)
                        .creditor(testUser1)
                        .debtor(testUser2)
                        .amount(50.0)
                        .build()));

        List<UserDebtResponse> debts = splitBillService.getDebtsForAuthenticatedUserByGroup(1);

        assertNotNull(debts);
        assertEquals(1, debts.size());
        assertEquals(50.0, debts.get(0).getAmount());
    }

    @Test
    void testSettleDebt_Success() {
        SettleDebtRequest request = new SettleDebtRequest();
        request.setGroupId(1);
        request.setCreditorEmail("user1@example.com");
        request.setAmount(50.0);

        when(authentication.getName()).thenReturn("user2@example.com");
        when(userRepository.findByEmail("user2@example.com")).thenReturn(Optional.of(testUser2));
        when(userRepository.findByEmail("user1@example.com")).thenReturn(Optional.of(testUser1));
        when(groupRepository.findById(1)).thenReturn(Optional.of(testGroup));
        when(groupPartnershipRepository.findByGroupAndCreditorAndDebtor(testGroup, testUser1, testUser2))
                .thenReturn(Optional.of(GroupPartnership.builder()
                        .group(testGroup)
                        .creditor(testUser1)
                        .debtor(testUser2)
                        .amount(50.0)
                        .build()));

        SettlementResponse response = splitBillService.settleDebt(request);

        assertNotNull(response);
        assertEquals(50.0, response.getAmountSettled());
    }

    @Test
    void testGetDebitCreditSummary() {
        when(authentication.getName()).thenReturn("user2@example.com");
        when(userRepository.findByEmail("user2@example.com")).thenReturn(Optional.of(testUser2));
        when(groupRepository.findById(1)).thenReturn(Optional.of(testGroup));
        when(groupPartnershipRepository.findByGroupAndDebtor(testGroup, testUser2))
                .thenReturn(Collections.singletonList(GroupPartnership.builder()
                        .group(testGroup)
                        .creditor(testUser1)
                        .debtor(testUser2)
                        .amount(30.0)
                        .build()));

        DebitCreditSummaryResponse summary = splitBillService.getDebitCreditSummary(1);

        assertNotNull(summary);
        assertEquals(30.0, summary.getTotalDebit());
        assertEquals(0.0, summary.getTotalCredit());
    }
}
