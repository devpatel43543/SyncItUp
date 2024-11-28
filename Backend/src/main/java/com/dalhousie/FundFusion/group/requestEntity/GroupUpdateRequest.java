package com.dalhousie.FundFusion.group.requestEntity;


import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupUpdateRequest {
    private String groupName;
    private String description;
    /**
     * Validates the group update request fields.
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