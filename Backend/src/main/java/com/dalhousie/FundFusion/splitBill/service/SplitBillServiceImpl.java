package com.dalhousie.FundFusion.splitBill.service;

import com.dalhousie.FundFusion.group.entity.Group;
import com.dalhousie.FundFusion.group.repository.GroupRepository;
import com.dalhousie.FundFusion.splitBill.entity.ExpenseShare;
import com.dalhousie.FundFusion.splitBill.entity.GroupPartnership;
import com.dalhousie.FundFusion.splitBill.entity.Transaction;
import com.dalhousie.FundFusion.splitBill.repository.ExpenseShareRepository;
import com.dalhousie.FundFusion.splitBill.repository.GroupPartnershipRepository;
import com.dalhousie.FundFusion.splitBill.repository.TransactionRepository;
import com.dalhousie.FundFusion.splitBill.requestEntity.AddExpenseRequest;
import com.dalhousie.FundFusion.splitBill.responseEntity.DebitCreditSummaryResponse;
import com.dalhousie.FundFusion.splitBill.requestEntity.SettleDebtRequest;
import com.dalhousie.FundFusion.splitBill.requestEntity.UpdateExpenseRequest;
import com.dalhousie.FundFusion.splitBill.responseEntity.*;
import com.dalhousie.FundFusion.user.entity.User;
import com.dalhousie.FundFusion.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SplitBillServiceImpl implements SplitBillService {

    private final TransactionRepository transactionRepository;
    private final ExpenseShareRepository expenseShareRepository;
    private final GroupPartnershipRepository groupPartnershipRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    @Override
    @Transactional
    public AddExpenseResponse createExpense(AddExpenseRequest request) {
        log.info("Creating expense with details: {}", request);

        // Validate the group and payer
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new IllegalArgumentException("Group not found with ID: " + request.getGroupId()));
        User paidBy = userRepository.findByEmail(request.getPaidByEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + request.getPaidByEmail()));

        // Create the transaction
        Transaction transaction = Transaction.builder()
                .group(group)
                .paidBy(paidBy)
                .amount(request.getAmount())
                .title(request.getTitle())
                .category(request.getCategory())
                .build();
        transactionRepository.save(transaction);

        // Calculate individual shares
        double individualShare = request.getAmount() / request.getInvolvedMembers().size();
        for (String memberEmail : request.getInvolvedMembers()) {
            User member = userRepository.findByEmail(memberEmail)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + memberEmail));
            expenseShareRepository.save(ExpenseShare.builder()
                    .transaction(transaction)
                    .user(member)
                    .shareAmount(individualShare)
                    .build());
            // Update group partnerships
            if (!member.equals(paidBy)) {
                updatePartnership(group, paidBy, member, individualShare);
            }
        }

        log.info("Expense created successfully.");
        return buildResponse(transaction);
    }

    @Override
    @Transactional
    public AddExpenseResponse updateExpense(UpdateExpenseRequest request) {
        log.info("Updating expense with ID: {}", request.getTransactionId());

        // Fetch the transaction
        Transaction transaction = transactionRepository.findById(request.getTransactionId())
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with ID: " + request.getTransactionId()));

        // Reverse previous partnerships
        List<ExpenseShare> shares = expenseShareRepository.findByTransaction(transaction);
        for (ExpenseShare share : shares) {
            if (!share.getUser().equals(transaction.getPaidBy())) {
                updatePartnership(transaction.getGroup(), transaction.getPaidBy(), share.getUser(), -share.getShareAmount());
            }
        }
        expenseShareRepository.deleteAll(shares);

        // Update transaction details
        transaction.setAmount(request.getAmount());
        transaction.setTitle(request.getTitle());
        transaction.setCategory(request.getCategory());
        transactionRepository.save(transaction);

        // Recalculate and update partnerships
        double individualShare = request.getAmount() / request.getInvolvedMembers().size();
        for (String memberEmail : request.getInvolvedMembers()) {
            User member = userRepository.findByEmail(memberEmail)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + memberEmail));
            expenseShareRepository.save(ExpenseShare.builder()
                    .transaction(transaction)
                    .user(member)
                    .shareAmount(individualShare)
                    .build());
            if (!member.equals(transaction.getPaidBy())) {
                updatePartnership(transaction.getGroup(), transaction.getPaidBy(), member, individualShare);
            }
        }

        log.info("Expense updated successfully.");
        return buildResponse(transaction);
    }

    @Override
    @Transactional
    public void deleteExpense(Long transactionId) {
        log.info("Deleting expense with ID: {}", transactionId);

        // Fetch the transaction
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with ID: " + transactionId));

        // Reverse partnerships
        List<ExpenseShare> shares = expenseShareRepository.findByTransaction(transaction);
        for (ExpenseShare share : shares) {
            if (!share.getUser().equals(transaction.getPaidBy())) {
                updatePartnership(transaction.getGroup(), transaction.getPaidBy(), share.getUser(), -share.getShareAmount());
            }
        }

        // Delete shares and transaction
        expenseShareRepository.deleteAll(shares);
        transactionRepository.delete(transaction);

        log.info("Expense deleted successfully.");
    }

    @Override
    public List<DebtResponse> getTransactionDebts(Integer groupId) {
        log.info("Fetching debts for group ID: {}", groupId);

        // Fetch the currently authenticated user
        //User currentUser = getCurrentAuthenticatedUser();

        // Fetch the group
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group with ID " + groupId + " not found."));

        // Fetch all expense shares for the group
        List<ExpenseShare> expenseShares = expenseShareRepository.findByTransactionGroup(group);

        // Prepare the list of DebtResponse
        List<DebtResponse> debtResponses = new ArrayList<>();

        for (ExpenseShare share : expenseShares) {
            // Skip self-transactions
            if (share.getTransaction().getPaidBy().equals(share.getUser())) {
                continue;
            }

            debtResponses.add(
                    DebtResponse.builder()
                            .paidByEmail(share.getTransaction().getPaidBy().getEmail())
                            .owesToEmail(share.getUser().getEmail())
                            .amount(share.getShareAmount())
                            .build()
            );
        }

        return debtResponses;
    }

    @Override
    public List<DebtResponse> getgroupdebtsummary(Integer groupId){
        log.info("Fetching overall debts for group ID: {}", groupId);

        // Fetch the currently authenticated user
        User currentUser = getCurrentAuthenticatedUser();

        // Fetch the group
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group with ID " + groupId + " not found."));

        // Fetch the partnership summary for the group
        List<GroupPartnership> partnerships = groupPartnershipRepository.findByGroup(group);

        // Prepare the list of DebtResponse
        List<DebtResponse> debtResponses = new ArrayList<>();

        for (GroupPartnership partnership : partnerships) {
            if (!partnership.getCreditor().equals(partnership.getDebtor())) { // Skip self-relationships
                debtResponses.add(
                        DebtResponse.builder()
                                .paidByEmail(partnership.getCreditor().getEmail())
                                .owesToEmail(partnership.getDebtor().getEmail())
                                .amount(partnership.getAmount())
                                .build()
                );
            }
        }

        return debtResponses;
    }

    @Override
    public List<TransactionSummaryResponse> getTransactionSummary(Integer groupId) {
        log.info("Fetching transaction summary for group ID: {}", groupId);

        // Fetch the group
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group with ID " + groupId + " not found."));

        // Fetch all transactions for the group
        List<Transaction> transactions = transactionRepository.findByGroup(group);

        // Prepare the list of TransactionSummaryResponse
        List<TransactionSummaryResponse> transactionSummaries = new ArrayList<>();

        for (Transaction transaction : transactions) {
            // Fetch involved members for the transaction
            List<ExpenseShare> shares = expenseShareRepository.findByTransaction(transaction);

            // Extract involved members' emails
            List<String> involvedMembersEmails = shares.stream()
                    .map(share -> share.getUser().getEmail())
                    .collect(Collectors.toList());

            transactionSummaries.add(
                    TransactionSummaryResponse.builder()
                            .transactionId(transaction.getId())
                            .amountPaid(transaction.getAmount())
                            .transactionDate(transaction.getCreatedAt())
                            .paidByEmail(transaction.getPaidBy().getEmail())
                            .involvedMembersCount(involvedMembersEmails.size())
                            .involvedMembers(involvedMembersEmails)
                            .title(transaction.getTitle())
                            .category(transaction.getCategory())
                            .build()
            );
        }

        log.info("Transaction summaries fetched successfully.");
        return transactionSummaries;
    }

    @Override
    public List<UserDebtResponse> getDebtsForAuthenticatedUserByGroup(Integer groupId) {
        User currentUser = getCurrentAuthenticatedUser();

        // Fetch the group
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found with ID: " + groupId));

        // Fetch all partnerships where the authenticated user is the debtor for the specific group
        List<GroupPartnership> partnerships = groupPartnershipRepository.findByGroupAndDebtor(group, currentUser);

        // Map partnerships to UserDebtResponse
        return partnerships.stream()
                .map(partnership -> UserDebtResponse.builder()
                        .creditorEmail(partnership.getCreditor().getEmail())
                        .groupId(partnership.getGroup().getId())
                        .groupName(partnership.getGroup().getGroupName())
                        .amount(partnership.getAmount())
                        .build())
                .collect(Collectors.toList());
    }

    // Settle a specific debt
    @Override
    @Transactional
    public SettlementResponse settleDebt(SettleDebtRequest request) {
        User currentUser = getCurrentAuthenticatedUser();
        User creditor = userRepository.findByEmail(request.getCreditorEmail())
                .orElseThrow(() -> new IllegalArgumentException("Creditor not found with email: " + request.getCreditorEmail()));

        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new IllegalArgumentException("Group not found with ID: " + request.getGroupId()));

        GroupPartnership partnership = groupPartnershipRepository.findByGroupAndCreditorAndDebtor(group, creditor, currentUser)
                .orElseThrow(() -> new IllegalArgumentException("No pending debt found with the creditor in this group."));

        if (request.getAmount() > partnership.getAmount()) {
            throw new IllegalArgumentException("Settlement amount exceeds the pending debt.");
        }

        partnership.setAmount(partnership.getAmount() - request.getAmount());

        if (partnership.getAmount() == 0) {
            groupPartnershipRepository.delete(partnership);
        } else {
            groupPartnershipRepository.save(partnership);
        }

        return SettlementResponse.builder()
                .debtorEmail(currentUser.getEmail())
                .creditorEmail(creditor.getEmail())
                .amountSettled(request.getAmount())
                .message("Settlement completed successfully.")
                .build();
    }
    @Override
    public DebitCreditSummaryResponse getDebitCreditSummary(Integer groupId) {
        // Get the currently authenticated user
        User currentUser = getCurrentAuthenticatedUser();
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found with ID: " + groupId));
        log.info("line 312:{}",currentUser);
        // Fetch all partnerships where the user is either a debtor or a creditor
        List<GroupPartnership> asDebtor = groupPartnershipRepository.findByGroupAndDebtor(group, currentUser);
        List<GroupPartnership> asCreditor = groupPartnershipRepository.findByGroupAndCreditor(group, currentUser);

        List<Transaction> groupTransactions = transactionRepository.findByGroup(group);
        double totalGroupExpense = groupTransactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
        // Calculate total debit (sum of amounts where the user is a debtor)
        double totalDebit = asDebtor.stream()
                .mapToDouble(GroupPartnership::getAmount)
                .sum();

        // Calculate total credit (sum of amounts where the user is a creditor)
        double totalCredit = asCreditor.stream()
                .mapToDouble(GroupPartnership::getAmount)
                .sum();

        // Return the result
        return DebitCreditSummaryResponse.builder()
                .userEmail(currentUser.getEmail())
                .totalDebit(totalDebit)
                .totalCredit(totalCredit)
                .totalGroupExpense(totalGroupExpense)
                .build();
    }

    //helper
    private void updatePartnership(Group group, User creditor, User debtor, double amount) {
        GroupPartnership partnership = groupPartnershipRepository.findByGroupAndCreditorAndDebtor(group, creditor, debtor)
                .orElse(GroupPartnership.builder()
                        .group(group)
                        .creditor(creditor)
                        .debtor(debtor)
                        .amount(0.0)
                        .build());
        partnership.setAmount(partnership.getAmount() + amount);
        if (partnership.getAmount() == 0) {
            groupPartnershipRepository.delete(partnership);
        } else {
            groupPartnershipRepository.save(partnership);
        }
    }


    private User getCurrentAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found."));
    }

    private AddExpenseResponse buildResponse(Transaction transaction) {
        return AddExpenseResponse.builder()
                .transactionId(transaction.getId())
                .groupName(transaction.getGroup().getGroupName())
                .totalAmount(transaction.getAmount())
                .paidByEmail(transaction.getPaidBy().getEmail())
                .title(transaction.getTitle())
                .category(transaction.getCategory())
                .build();
    }
}