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
}