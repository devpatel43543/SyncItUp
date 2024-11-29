package com.dalhousie.FundFusion.group.requestEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupRequest {
    private String groupName;
    private String description;
    private List<String> memberEmail;
    /**
     * Validates the group request fields.
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
        if (isInvalidMemberEmails(memberEmail)) {
            throw new IllegalArgumentException("Member emails list cannot be null or empty and must contain valid email addresses.");
        }
    }

    private boolean isInvalidGroupName(String groupName) {
        return isNullOrBlank(groupName);
    }

    private boolean isInvalidDescription(String description) {
        return isNullOrBlank(description);
    }

    private boolean isInvalidMemberEmails(List<String> emails) {
        if (emails == null || emails.isEmpty()) {
            return true;
        }
        for (String email : emails) {
            if (isNullOrBlank(email) || isInvalidEmailFormat(email)) {
                return true;
            }
        }
        return false;
    }

    private boolean isInvalidEmailFormat(String email) {
        return !email.contains("@");
    }

    private boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }
}
