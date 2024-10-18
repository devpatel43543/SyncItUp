package com.dalhousie.FundFusion.group.service;

import com.dalhousie.FundFusion.exaption.UserNotFoundException;
import com.dalhousie.FundFusion.group.entity.Group;
import com.dalhousie.FundFusion.group.entity.PendingGroupMembers;
import com.dalhousie.FundFusion.group.entity.UserGroup;
import com.dalhousie.FundFusion.group.repository.GroupRepository;
import com.dalhousie.FundFusion.group.repository.PendingGroupMembersRepository;
import com.dalhousie.FundFusion.group.repository.UserGroupRepository;
import com.dalhousie.FundFusion.group.requestEntity.GroupRequest;
import com.dalhousie.FundFusion.group.requestEntity.GroupUpdateRequest;
import com.dalhousie.FundFusion.group.responseEntity.GroupResponse;
import com.dalhousie.FundFusion.group.responseEntity.GroupSummaryResponse;
import com.dalhousie.FundFusion.user.entity.User;
import com.dalhousie.FundFusion.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService{
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final PendingGroupMembersRepository pendingGroupMembershipRepository;
    @Override
    public GroupResponse createGroup(GroupRequest groupRequest) {
        String creatorEmail = getCurrentAuthenticatedUserEmail(); //current authenticated user

        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new UserNotFoundException("user not found"));

        Group group = Group.builder()
                .groupName(groupRequest.getGroupName())
                .description(groupRequest.getDescription())
                .creator(creator)
                .build();
        groupRepository
                .save(group);

        for (String email : groupRequest.getMemberEmail()) {
            User user = userRepository.findByEmail(email).orElse(null);

            if (user != null) {
                // User exists, link directly
                UserGroup userGroup = UserGroup.builder()
                        .user(user)
                        .group(group)
                        .userEmail(user.getEmail())
                        .build();

                userGroupRepository.save(userGroup);
            } else {
                // User does not exist, create pending membership

                //check user, already in pending_group_membership table
                PendingGroupMembers existingPending = pendingGroupMembershipRepository
                        .findByEmailAndGroup(email, group);

                if(existingPending==null) {


                    PendingGroupMembers pending = PendingGroupMembers.builder()
                            .email(email)
                            .group(group)
                            .status("PENDING")
                            .invitedAt(LocalDateTime.now())
                            .build();

                    pendingGroupMembershipRepository.save(pending);
                }
            }
        }

        return GroupResponse.builder()
                .groupName(groupRequest.getGroupName())
                .description(groupRequest.getDescription())
                .creatorEmail(creatorEmail)
                .members(groupRequest.getMemberEmail())
                .build();
    }

    @Override
    public GroupResponse deleteGroupMember(Integer groupId, Integer memberId) {
        String currentUserEmail = getCurrentAuthenticatedUserEmail(); // Get current authenticated user
        log.info("Current user email: {}", groupId);

        // Fetch the group
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        log.info("Current user email: {}", currentUserEmail);
        log.info("Group creator email: {}", group.getCreator().getEmail());        // Ensure the current user is the group creator

        if (!group.getCreator().getEmail().equals(currentUserEmail)) {
            throw new RuntimeException("Only the group creator can remove members");
        }

        // Fetch the user group membership by userId and groupId
        UserGroup userGroup = userGroupRepository.findByUserIdAndGroupId(memberId, groupId)
                .orElseThrow(() -> new RuntimeException("User is not a member of this group"));

        log.info("Deleting user with ID: {} from group: {}", memberId, groupId);

        // Delete the user from the group
        userGroupRepository.delete(userGroup);

        // after deletion member list
        List<String> updatedMemberEmails = group.getUserGroups()
                .stream()
                .map(UserGroup::getUserEmail) // Get member email addresses
                .toList(); // Collect to a list

        return GroupResponse.builder()
                .groupName(group.getGroupName())
                .description(group.getDescription())
                .creatorEmail(group.getCreator().getEmail())
                .members(updatedMemberEmails)
                .build();
    }

    @Override
    public GroupResponse updateGroup(Integer groupId, GroupUpdateRequest updateRequest) {
        String currentUserEmail = getCurrentAuthenticatedUserEmail(); // Get current authenticated user

        // Fetch the group
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Ensure the current user is the group creator
        if (!group.getCreator().getEmail().equals(currentUserEmail)) {
            throw new RuntimeException("Only the group creator can update group information");
        }

        // Update the group information
        if (updateRequest.getGroupName() != null) {
            group.setGroupName(updateRequest.getGroupName());
        }
        if (updateRequest.getDescription() != null) {
            group.setDescription(updateRequest.getDescription());
        }

        // Save the updated group
        groupRepository.save(group);
        // after deletion member list
        List<String> updatedMemberEmails = group.getUserGroups()
                .stream()
                .map(UserGroup::getUserEmail) // Get member email addresses
                .toList(); // Collect to a list

        return GroupResponse.builder()
                .groupName(group.getGroupName())
                .description(group.getDescription())
                .creatorEmail(group.getCreator().getEmail())
                .members(updatedMemberEmails)
                .build();
    }

    @Override
    public List<GroupSummaryResponse> getAllGroups() {
        List<Group> groups = groupRepository.findAll(); // Fetch all groups
        // after deletion member list

        // Convert the groups to GroupResponse objects
        return groups.stream().map(group -> GroupSummaryResponse.builder()
                .groupId(group.getId())
                .groupName(group.getGroupName())
                .description(group.getDescription())
                .creatorEmail(group.getCreator().getEmail())
                .build()).collect(Collectors.toList());
    }

    @Override
    public GroupResponse getGroupById(Integer groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        List<String> memberEmail = group.getUserGroups()
                .stream()
                .map(UserGroup::getUserEmail) // Get member email addresses
                .toList(); // Collect to a list

        // Convert to GroupDetailResponse
        return GroupResponse.builder()
                .groupName(group.getGroupName())
                .description(group.getDescription())
                .creatorEmail(group.getCreator().getEmail())
                .members(memberEmail)
                .build();
    }
    private String getCurrentAuthenticatedUserEmail() {
        // Get authenticated user's details
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();  // email in this case
        } else {
            throw new RuntimeException("User not authenticated");
        }
    }
}
