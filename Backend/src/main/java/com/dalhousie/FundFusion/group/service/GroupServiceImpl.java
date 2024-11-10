package com.dalhousie.FundFusion.group.service;

import com.dalhousie.FundFusion.exception.AccessDeniedException;
import com.dalhousie.FundFusion.exception.GroupAlreadyExistsException;
import com.dalhousie.FundFusion.exception.MemberNotExist;
import com.dalhousie.FundFusion.exception.UserNotFoundException;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        log.info("line number 41 :{}",groupRequest.getMemberEmail());
        String creatorEmail = getCurrentAuthenticatedUserEmail();
        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Check if group name already exists
        if (groupRepository.existsByGroupName(groupRequest.getGroupName())) {
            throw new GroupAlreadyExistsException("Group name already exists. Please choose a different name.");
        }

        // Save the group
        Group group = saveGroup(groupRequest, creator);
        addMemberIfNotExists(group, creator.getEmail(), true); // Add creator as a member

        // Collect unique emails to avoid duplicate entry
        Set<String> uniqueEmails = groupRequest.getMemberEmail() != null
                ? new HashSet<>(groupRequest.getMemberEmail())
                : new HashSet<>();

        // Add other members
        for (String email : uniqueEmails) {
            addMemberIfNotExists(group, email, false);
        }

        // Prepare response including creator and all other members
        uniqueEmails.add(creator.getEmail());

        return GroupResponse.builder()
                .groupName(groupRequest.getGroupName())
                .description(groupRequest.getDescription())
                .creatorEmail(creatorEmail)
                .members(new ArrayList<>(uniqueEmails)) // List of all unique members
                .build();
    }

    @Override
    public GroupResponse addGroupMembers(Integer groupId, List<String> newMemberEmails) {
        Group group = validateGroupMembership(groupId); // for validation if the current user is a group member
        log.info("line 80:{}",groupId);

        // Add new members to the group
        Set<String> uniqueEmails = new HashSet<>(newMemberEmails);
        for (String email : uniqueEmails) {
            addMemberIfNotExists(group, email, false);
        }
        List<String> allMembers = getGroupMembersEmails(group);


        return new GroupResponse(
                group.getGroupName(),
                group.getDescription(),
                group.getCreator().getEmail(),
                allMembers
        );
    }

    @Override
    public GroupResponse removeGroupMember(Integer groupId, String memberEmail) {
        Group group = validateGroupCreator(groupId);

        // Fetch the user group membership by userId and groupId
        Optional<UserGroup> userGroup = userGroupRepository.findByUserEmailAndGroupId(memberEmail,groupId);
        //check in both userGroup and pending table
        if(userGroup.isPresent()) {
            UserGroup userGroupUser = userGroup.get();
            userGroupRepository.delete(userGroupUser);
        }else{
            //check the pending table
            Optional<PendingGroupMembers> pendingGroupMembers = pendingGroupMembershipRepository.findByEmailAndGroupId(memberEmail,groupId);
            if(pendingGroupMembers.isPresent()) {
                PendingGroupMembers pendingGroupMembersUser = pendingGroupMembers.get();
                pendingGroupMembershipRepository.delete(pendingGroupMembersUser);
            }else{
                throw new MemberNotExist("User is not a member of this group");
            }
        }



        List<String> updatedMembers = getGroupMembersEmails(group);

        return GroupResponse.builder()
                .groupName(group.getGroupName())
                .description(group.getDescription())
                .creatorEmail(group.getCreator().getEmail())
                .members(updatedMembers)
                .build();
    }

    @Override
    public GroupResponse updateGroup(Integer groupId, GroupUpdateRequest updateRequest) {
        Group group = validateGroupCreator(groupId);

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
        List<String> updatedMembers = getGroupMembersEmails(group);

        return GroupResponse.builder()
                .groupName(group.getGroupName())
                .description(group.getDescription())
                .creatorEmail(group.getCreator().getEmail())
                .members(updatedMembers)
                .build();
    }

    @Override
    public List<GroupSummaryResponse> getAllGroups() {
        String currentUserEmail = getCurrentAuthenticatedUserEmail();

        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        List<Group> userGroups = getUserGroups(user);

        return userGroups.stream().map(group -> {
            // Get confirmed and pending member emails
            List<String> confirmedEmails = group.getUserGroups().stream()
                    .map(userGroup -> userGroup.getUser().getEmail())
                    .collect(Collectors.toList());

            List<String> pendingEmails = pendingGroupMembershipRepository.findByGroupId(group.getId()).stream()
                    .map(PendingGroupMembers::getEmail)
                    .collect(Collectors.toList());

            Set<String> uniqueEmails = new HashSet<>(confirmedEmails);
            uniqueEmails.addAll(pendingEmails);

            return GroupSummaryResponse.builder()
                    .groupId(group.getId())
                    .groupName(group.getGroupName())
                    .description(group.getDescription())
                    .creatorEmail(group.getCreator().getEmail())
                    .members(new ArrayList<>(uniqueEmails))
                    .build();
        }).collect(Collectors.toList());
    }


    @Override
    public GroupResponse getGroupById(Integer groupId) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));


        // Convert to GroupDetailResponse
        return GroupResponse.builder()
                .groupName(group.getGroupName())
                .description(group.getDescription())
                .creatorEmail(group.getCreator().getEmail())
                .members(getGroupMembersEmails(group))
                .build();
    }



   // all helper methods
    private Group validateGroupCreator(Integer groupId) {
        String currentUser = getCurrentAuthenticatedUserEmail();
        log.info("line 201:{}",currentUser);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        log.info("line 203:{}",group.getCreator().getEmail());

        if (!group.getCreator().getEmail().equals(currentUser)) {
            throw new AccessDeniedException("Only the group creator can modify members");
        }
        return group;
    }

    private Group validateGroupMembership(Integer groupId) {

        String currentUserEmail = getCurrentAuthenticatedUserEmail();
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        boolean isMember = group.getUserGroups().stream()
                .anyMatch(userGroup -> userGroup.getUser().getEmail().equals(currentUserEmail));

        if (!isMember) {
            throw new RuntimeException("Only group members can add new members");
        }

        return group;
    }
    private boolean addMemberIfNotExists(Group group, String email, boolean isCreator) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            boolean alreadyMember = userGroupRepository.findByUserIdAndGroupId(user.getId(), group.getId()).isPresent();
            if (!alreadyMember) {
                userGroupRepository.save(new UserGroup(null, email, user, group));
                return true;
            }
        } else if (!isCreator) {
            addPendingMember(group, email);
            return true;
        }
        return false;
    }
    private void addPendingMember(Group group, String email) {
        PendingGroupMembers existingPending = pendingGroupMembershipRepository.findByEmailAndGroup(email, group);
        if (existingPending == null) {
            pendingGroupMembershipRepository.save(PendingGroupMembers.builder()
                    .email(email)
                    .group(group)
                    .status("PENDING")
                    .invitedAt(LocalDateTime.now())
                    .build());
        }
    }
    private Group saveGroup(GroupRequest groupRequest, User creator) {
        Group group = Group.builder()
                .groupName(groupRequest.getGroupName())
                .description(groupRequest.getDescription())
                .creator(creator)
                .build();
        return groupRepository.save(group);
    }

    private List<Group> getUserGroups(User user) {
        // Get confirmed member
        List<Group> confirmedGroups = userGroupRepository.findByUserId(user.getId()).stream()
                .map(UserGroup::getGroup)
                .collect(Collectors.toList());

        // Get pending member
        List<Group> pendingGroups = pendingGroupMembershipRepository.findByEmail(user.getEmail()).stream()
                .map(PendingGroupMembers::getGroup)
                .collect(Collectors.toList());

        // Combine unique set
        Set<Group> allGroups = new HashSet<>(confirmedGroups);
        allGroups.addAll(pendingGroups);
        return new ArrayList<>(allGroups);
    }
    private List<String> getGroupMembersEmails(Group group) {
        // Get confirmed member
        List<String> confirmedEmails = group.getUserGroups().stream()
                .map(userGroup -> userGroup.getUser().getEmail())
                .collect(Collectors.toList());

        // Get pending member
        List<String> pendingEmails = pendingGroupMembershipRepository.findByGroupId(group.getId()).stream()
                .map(PendingGroupMembers::getEmail)
                .collect(Collectors.toList());

        // Combine unique set
        Set<String> allEmails = new HashSet<>(confirmedEmails);
        allEmails.addAll(pendingEmails);

        return new ArrayList<>(allEmails);
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
