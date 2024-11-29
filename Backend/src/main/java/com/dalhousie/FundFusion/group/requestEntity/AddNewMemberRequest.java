package com.dalhousie.FundFusion.group.requestEntity;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddNewMemberRequest {
    private List<String> newMemberEmails;

    /**
     * Validates the new member emails.
     *
     * @throws IllegalArgumentException if the list is null, empty, or contains invalid email addresses.
     */
    public void validate() {
        if (isInvalidMemberEmails(newMemberEmails)) {
            throw new IllegalArgumentException("New member emails list cannot be null, empty, or contain invalid email addresses.");
        }
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

    private boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean isInvalidEmailFormat(String email) {
        return !email.contains("@");
    }
}
