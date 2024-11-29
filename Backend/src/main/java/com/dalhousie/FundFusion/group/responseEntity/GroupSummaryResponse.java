package com.dalhousie.FundFusion.group.responseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupSummaryResponse {
    private Integer groupId;
    private String groupName;
    private String description;
    private String creatorEmail;
    private List<String> members; // New field for member emails

    /**
     * Validates the group summary response fields.
     *
     * @throws IllegalArgumentException if any field is invalid.
     */
    public void validate() {
        if (groupId == null || groupId < 0) {
            throw new IllegalArgumentException("Group ID must be a positive integer.");
        }
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
