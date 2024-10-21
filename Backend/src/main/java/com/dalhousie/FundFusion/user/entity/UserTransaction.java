package com.dalhousie.FundFusion.user.entity;

import com.dalhousie.FundFusion.category.entity.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.lang.NonNull;

import java.time.LocalDate;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer txnId;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
    private String txnDesc;
    @NotNull(message = "Expense cannot be empty")
    private Float expense;
    private LocalDate txnDate;
    @ManyToOne
    @JoinColumn(name = "categoryId")
    @NotNull(message = "Transaction category is mandatory")
    private Category category;

}
