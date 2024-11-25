package com.dalhousie.FundFusion.splitBill.entity;


import com.dalhousie.FundFusion.group.entity.Group;
import com.dalhousie.FundFusion.user.entity.User;
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
@Table(name = "group_partnerships")
public class GroupPartnership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne
    @JoinColumn(name = "creditor_id", nullable = false)
    private User creditor; // The user who is owed money

    @ManyToOne
    @JoinColumn(name = "debtor_id", nullable = false)
    private User debtor; // The user who owes money

    @Column(nullable = false)
    private Double amount; // The amount owed
}

