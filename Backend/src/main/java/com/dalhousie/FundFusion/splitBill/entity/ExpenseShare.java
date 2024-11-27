package com.dalhousie.FundFusion.splitBill.entity;

import com.dalhousie.FundFusion.user.entity.User;
import com.dalhousie.FundFusion.splitBill.entity.Transaction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "expense_shares")
public class ExpenseShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction; // Links each share to a transaction

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // User involved in the expense

    @Column(nullable = false)
    private Double shareAmount; // The amount owed by this user
}
