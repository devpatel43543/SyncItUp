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
}
