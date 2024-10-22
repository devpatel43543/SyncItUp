package com.dalhousie.FundFusion.category.entity;

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
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int categoryId;
    private String categoryName;
    private boolean isDefault;

    @JoinColumn(name = "user_id")
    @ManyToOne
    private User user;

}
