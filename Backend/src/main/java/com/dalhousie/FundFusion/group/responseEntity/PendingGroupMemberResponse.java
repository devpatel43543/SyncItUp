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
}
