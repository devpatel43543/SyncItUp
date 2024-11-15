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
import com.dalhousie.FundFusion.group.responseEntity.PendingGroupMemberResponse;
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
        Group group = saveGroup(groupRequest, creator);
        addMemberIfNotExists(group, creator.getEmail(), true); // Add creator as a member

        Set<String> uniqueEmails = groupRequest.getMemberEmail() != null
                ? new HashSet<>(groupRequest.getMemberEmail())
                : new HashSet<>();

        for (String email : uniqueEmails) {
            addMemberIfNotExists(group, email, false);
        }

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
    public List<String> allMemberEmails(Integer groupId) {
        List<String> registeredEmails = userGroupRepository.findByGroupId(groupId).stream()
                .map(userGroup -> userGroup.getUser().getEmail())
                .collect(Collectors.toList());

        List<String> pendingEmails = pendingGroupMembershipRepository.findByGroupId(groupId).stream()
                .map(PendingGroupMembers::getEmail)
                .collect(Collectors.toList());

        Set<String> allEmails = new HashSet<>();
        allEmails.addAll(registeredEmails);
        allEmails.addAll(pendingEmails);

        return new ArrayList<>(allEmails);
    }

    @Override
    public GroupResponse removeGroupMember(Integer groupId, String memberEmail) {
        Group group = validateGroupCreator(groupId);

        Optional<UserGroup> userGroup = userGroupRepository.findByUserEmailAndGroupId(memberEmail,groupId);
        if(userGroup.isPresent()) {
            UserGroup userGroupUser = userGroup.get();
            userGroupRepository.delete(userGroupUser);
        }else{
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

        if (updateRequest.getGroupName() != null) {
            group.setGroupName(updateRequest.getGroupName());
        }
        if (updateRequest.getDescription() != null) {
            group.setDescription(updateRequest.getDescription());
        }

        groupRepository.save(group);
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
            List<String> memberEmails = getGroupMembersEmails(group);
            return GroupSummaryResponse.builder()
                    .groupId(group.getId())
                    .groupName(group.getGroupName())
                    .description(group.getDescription())
                    .creatorEmail(group.getCreator().getEmail())
                    .members(memberEmails)
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
    @Override
    public void acceptPendingMember(Integer groupId) {
        String email = getCurrentAuthenticatedUserEmail();

        PendingGroupMembers pendingMember = pendingGroupMembershipRepository.findByEmailAndGroupId(email, groupId)
                .orElseThrow(() -> new MemberNotExist("User is not a pending member of this group"));

        // Move to UserGroup
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not registered"));

        UserGroup userGroup = UserGroup.builder()
                .user(user)
                .group(pendingMember.getGroup())
                .userEmail(email)
                .build();
        userGroupRepository.save(userGroup);

        pendingGroupMembershipRepository.delete(pendingMember);
    }
    @Override
    public void rejectPendingMember(Integer groupId) {
        String email = getCurrentAuthenticatedUserEmail();
        PendingGroupMembers pendingMember = pendingGroupMembershipRepository.findByEmailAndGroupId(email, groupId)
                .orElseThrow(() -> new MemberNotExist("User is not a pending member of this group"));

        pendingGroupMembershipRepository.delete(pendingMember);
    }

    @Override
    public List<PendingGroupMemberResponse> getAllPendingRequest() {
        String email = getCurrentAuthenticatedUserEmail();
        List<PendingGroupMembers> pendingRequests = pendingGroupMembershipRepository.findByEmail(email);
        return pendingRequests.stream()
                .map(pendingMember -> PendingGroupMemberResponse.builder()
                        .groupId(pendingMember.getGroup().getId())
                        .userEmail(pendingMember.getEmail())
                        .groupName(pendingMember.getGroup().getGroupName())
                        .creatorEmail(pendingMember.getGroup().getCreator().getEmail())
                        .build())
                .collect(Collectors.toList());
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
        if (isCreator) {
            userGroupRepository.save(new UserGroup(null, email, user, group));
            return true;
        }

        if (user != null && userGroupRepository.findByUserIdAndGroupId(user.getId(), group.getId()).isPresent()) {
            return false;
        }

        addPendingMember(group, email);
        return true;
    }
    private void addPendingMember(Group group, String email) {
        PendingGroupMembers existingPending = pendingGroupMembershipRepository.findByEmailAndGroup(email, group);
        if (existingPending == null) {
            pendingGroupMembershipRepository.save(PendingGroupMembers.builder()
                    .email(email)
                    .group(group)
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
        List<Group> confirmedGroups = userGroupRepository.findByUserId(user.getId()).stream()
                .map(UserGroup::getGroup)
                .collect(Collectors.toList());

        Set<Group> allGroups = new HashSet<>(confirmedGroups);return new ArrayList<>(allGroups);
    }
    private List<String> getGroupMembersEmails(Group group) {
        List<String> confirmedEmails = group.getUserGroups().stream()
                .map(userGroup -> userGroup.getUser().getEmail())
                .collect(Collectors.toList());

        return confirmedEmails;
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
