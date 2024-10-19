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
}
