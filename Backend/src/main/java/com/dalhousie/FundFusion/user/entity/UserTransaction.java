package com.dalhousie.FundFusion.user.entity;

import com.dalhousie.FundFusion.category.entity.Category;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
@Entity
@Getter
@Setter
public class UserTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int txnId;
    private String txnDesc;
    private long expense;
    private LocalDate txnDate;
    @OneToOne
    @JoinColumn(name = "categoryId")
    private Category category;

}
