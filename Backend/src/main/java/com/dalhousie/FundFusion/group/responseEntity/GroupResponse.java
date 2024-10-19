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
}
