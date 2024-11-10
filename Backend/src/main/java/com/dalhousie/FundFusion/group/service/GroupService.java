package com.dalhousie.FundFusion.group.service;

import com.dalhousie.FundFusion.group.requestEntity.GroupRequest;
import com.dalhousie.FundFusion.group.requestEntity.GroupUpdateRequest;
import com.dalhousie.FundFusion.group.responseEntity.GroupResponse;
import com.dalhousie.FundFusion.group.responseEntity.GroupSummaryResponse;

import java.util.List;

public interface GroupService {
    GroupResponse createGroup(GroupRequest groupRequest);
    GroupResponse removeGroupMember(Integer groupId, String memberEmail);
    GroupResponse updateGroup(Integer groupId, GroupUpdateRequest updateRequest);
    List<GroupSummaryResponse> getAllGroups();
    GroupResponse getGroupById(Integer groupId);
    GroupResponse addGroupMembers(Integer groupId, List<String> newMemberEmails);
}

