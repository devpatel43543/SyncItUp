package com.dalhousie.FundFusion.group.entity;

import com.dalhousie.FundFusion.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_group")
public class Group  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String groupName;

    @Column(nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User creator;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<UserGroup> userGroups;
    /**
     * Validates the group fields.
     *
     * @throws IllegalArgumentException if any field is invalid.
     */
    public void validate() {
        if (isInvalidGroupName(groupName)) {
            throw new IllegalArgumentException("Group name cannot be null or empty.");
        }
        if (isInvalidDescription(description)) {
            throw new IllegalArgumentException("Description cannot be null or empty.");
        }
        if (creator == null) {
            throw new IllegalArgumentException("Group creator must be specified.");
        }
    }

    private boolean isInvalidGroupName(String groupName) {
        return isNullOrBlank(groupName);
    }

    private boolean isInvalidDescription(String description) {
        return isNullOrBlank(description);
    }

    private boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

}
