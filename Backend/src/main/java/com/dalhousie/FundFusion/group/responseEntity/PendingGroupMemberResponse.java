package com.dalhousie.FundFusion.group.responseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PendingGroupMemberResponse {
    private Integer groupId;
    private String userEmail;
    private String groupName;
    private String creatorEmail;
    /**
     * Validates the pending group member response fields.
     *
     * @throws IllegalArgumentException if any field is invalid.
     */
    public void validate() {
        if (groupId == null || groupId < 0) {
            throw new IllegalArgumentException("Group ID must be a positive integer.");
        }
        if (isNullOrBlank(userEmail) || isInvalidEmailFormat(userEmail)) {
            throw new IllegalArgumentException("Invalid user email address.");
        }
        if (isNullOrBlank(groupName)) {
            throw new IllegalArgumentException("Group name cannot be null or empty.");
        }
        if (isNullOrBlank(creatorEmail) || isInvalidEmailFormat(creatorEmail)) {
            throw new IllegalArgumentException("Invalid creator email address.");
        }
    }

    private boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean isInvalidEmailFormat(String email) {
        return !email.contains("@");
    }
}
