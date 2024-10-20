package com.dalhousie.FundFusion.user.entity;

import com.dalhousie.FundFusion.category.entity.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

import java.time.LocalDate;

@Data
@Entity
@Getter
@Setter
public class UserTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer txnId;
    @OneToOne
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
