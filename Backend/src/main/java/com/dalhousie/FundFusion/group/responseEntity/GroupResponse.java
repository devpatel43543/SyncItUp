package com.dalhousie.FundFusion.group.responseEntity;

import com.dalhousie.FundFusion.group.entity.UserGroup;
import com.dalhousie.FundFusion.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupResponse {
    private String groupName;
    private String description;
    private String creatorEmail;
    private List<String> members;
    /**
     * Validates the group response fields.
     *
     * @throws IllegalArgumentException if any field is invalid.
     */
    public void validate() {
        if (isNullOrBlank(groupName)) {
            throw new IllegalArgumentException("Group name cannot be null or empty.");
        }
        if (isNullOrBlank(description)) {
            throw new IllegalArgumentException("Description cannot be null or empty.");
        }
        if (isNullOrBlank(creatorEmail) || isInvalidEmailFormat(creatorEmail)) {
            throw new IllegalArgumentException("Invalid creator email address.");
        }
        if (members == null) {
            throw new IllegalArgumentException("Members list cannot be null.");
        }
    }

    private boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean isInvalidEmailFormat(String email) {
        return !email.contains("@");
    }
}
