package com.dalhousie.FundFusion.group.entity;
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
@Table(name = "user_group_junction")
public class UserGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String userEmail;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    public void validate() {
        if (isInvalidEmail(userEmail)) {
            throw new IllegalArgumentException("Invalid user email address.");
        }
        if (user == null) {
            throw new IllegalArgumentException("User must be specified.");
        }
        if (group == null) {
            throw new IllegalArgumentException("Group must be specified.");
        }
    }

    private boolean isInvalidEmail(String email) {
        return isNullOrBlank(email) || isInvalidFormat(email);
    }

    private boolean isInvalidFormat(String email) {
        return !email.contains("@");
    }

    private boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

}
