package com.dalhousie.FundFusion.group.service;

import com.dalhousie.FundFusion.exception.AccessDeniedException;
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
    private final PendingGroupMembersRepository pendingRepo;
    
    @Override
    public GroupResponse createGroup(GroupRequest groupRequest) {
        log.info("line number 41 :{}",groupRequest.getMemberEmail());
        String creatorEmail = getCurrentAuthenticatedUserEmail();
        User creator = getUserByEmail(creatorEmail);
        Group group = saveGroup(groupRequest, creator);
        addMemberIfNotExists(group, creator.getEmail(), true); // Add creator as a member

        Set<String> uniqueEmails = getUniqueEmails(groupRequest.getMemberEmail());

        for (String email : uniqueEmails) {
            addMemberIfNotExists(group, email, false);
        }
        uniqueEmails.add(creator.getEmail());
        List<String> memberList = new ArrayList<>(uniqueEmails);
        var response = GroupResponse.builder()
                .groupName(groupRequest.getGroupName())
                .description(groupRequest.getDescription())
                .creatorEmail(creatorEmail)
                .members(memberList)
                .build();
        return response;
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
        Set<String> allEmails = new HashSet<>();
        allEmails.addAll(getRegisteredEmails(groupId));
        allEmails.addAll(getPendingEmails(groupId));
        return new ArrayList<>(allEmails);
    }

    @Override
    public GroupResponse removeGroupMember(Integer groupId, String memberEmail) {
        Group group = validateGroupCreator(groupId);


        if (!removeRegisteredMember(groupId, memberEmail) && !removePendingMember(groupId, memberEmail)) {
            throw new MemberNotExist("User is not a member of this group");
        }
        var responseBuilder = GroupResponse.builder()
                .groupName(group.getGroupName())
                .description(group.getDescription())
                .creatorEmail(group.getCreator().getEmail())
                .members(getGroupMembersEmails(group))
                .build();
        return responseBuilder;
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
        var responseBuilder = GroupResponse.builder()
                .groupName(group.getGroupName())
                .description(group.getDescription())
                .creatorEmail(group.getCreator().getEmail())
                .members(getGroupMembersEmails(group))
                .build();
        return responseBuilder;
    }

    @Override
    public List<GroupSummaryResponse> getAllGroups() {
        String currentUserEmail = getCurrentAuthenticatedUserEmail();
        User user = getUserByEmail(currentUserEmail);

        return getUserGroups(user).stream()
                .map(this::mapToGroupSummaryResponse)
                .collect(Collectors.toList());
    }


    @Override
    public GroupResponse getGroupById(Integer groupId) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));


        // Convert to GroupDetailResponse
        var responseBuilder = GroupResponse.builder()
                .groupName(group.getGroupName())
                .description(group.getDescription())
                .creatorEmail(group.getCreator().getEmail())
                .members(getGroupMembersEmails(group))
                .build();
        return responseBuilder;
    }
    @Override
    public void acceptPendingMember(Integer groupId) {
        String email = getCurrentAuthenticatedUserEmail();
        PendingGroupMembers pendingMember = findPendingMember(groupId, email);

        // Move to UserGroup
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not registered"));

        UserGroup userGroup = UserGroup.builder()
                .user(user)
                .group(pendingMember.getGroup())
                .userEmail(email)
                .build();
        userGroupRepository.save(userGroup);

        pendingRepo.delete(pendingMember);
    }
    @Override
    public void rejectPendingMember(Integer groupId) {
        String email = getCurrentAuthenticatedUserEmail();
        PendingGroupMembers pendingMember = findPendingMember(groupId, email);

        pendingRepo.delete(pendingMember);
    }

    @Override
    public List<PendingGroupMemberResponse> getAllPendingRequest() {
        String email = getCurrentAuthenticatedUserEmail();
        return pendingRepo.findByEmail(email).stream()
                .map(this::mapToPendingResponse)
                .collect(Collectors.toList());
    }

   // all helper methods
   private Set<String> getUniqueEmails(List<String> memberEmails) {
       return memberEmails != null ? new HashSet<>(memberEmails) : new HashSet<>();
   }
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
    private List<String> getRegisteredEmails(Integer groupId) {
        var userGroups = userGroupRepository.findByGroupId(groupId);
        var emails = userGroups.stream()
                .map(userGroup -> userGroup.getUser().getEmail())
                .collect(Collectors.toList());
        return emails;
    }

    private List<String> getPendingEmails(Integer groupId) {
        var pendingMembers = pendingRepo.findByGroupId(groupId);
        var emails = pendingMembers.stream()
                .map(PendingGroupMembers::getEmail)
                .collect(Collectors.toList());
        return emails;
    }

    private boolean removeRegisteredMember(Integer groupId, String memberEmail) {
        var optionalUserGroup = userGroupRepository.findByUserEmailAndGroupId(memberEmail, groupId);
        if (optionalUserGroup.isPresent()) {
            userGroupRepository.delete(optionalUserGroup.get());
            return true;
        }
        return false;
    }
    private PendingGroupMembers findPendingMember(Integer groupId, String email) {
        var pendingMember = pendingRepo.findByEmailAndGroupId(email, groupId);
        return pendingMember.orElseThrow(() -> new MemberNotExist("User is not a pending member of this group"));
    }

    private boolean removePendingMember(Integer groupId, String memberEmail) {
        return pendingRepo.findByEmailAndGroupId(memberEmail, groupId)
                .map(pendingMember -> {
                    pendingRepo.delete(pendingMember);
                    return true;
                }).orElse(false);
    }

    private GroupSummaryResponse mapToGroupSummaryResponse(Group group) {
        Integer groupId = group.getId();
        String groupName = group.getGroupName();
        String description = group.getDescription();
        String creatorEmail = group.getCreator().getEmail();
        List<String> memberEmails = getGroupMembersEmails(group);
        GroupSummaryResponse.GroupSummaryResponseBuilder responseBuilder = GroupSummaryResponse.builder()
                .groupId(groupId)
                .groupName(groupName)
                .description(description)
                .creatorEmail(creatorEmail)
                .members(memberEmails);
        return responseBuilder.build();

    }

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

        Stream<UserGroup> userGroupsStream = group.getUserGroups().stream();
        boolean isMember = userGroupsStream.anyMatch(userGroup ->
                userGroup.getUser().getEmail().equals(currentUserEmail));

        if (!isMember) {
            throw new RuntimeException("Only group members can add new members");
        }

        return group;
    }
    private PendingGroupMemberResponse mapToPendingResponse(PendingGroupMembers pendingMember) {
        Integer groupId = pendingMember.getGroup().getId();
        String userEmail = pendingMember.getEmail();
        String groupName = pendingMember.getGroup().getGroupName();
        String creatorEmail = pendingMember.getGroup().getCreator().getEmail();

        return PendingGroupMemberResponse.builder()
                .groupId(groupId)
                .userEmail(userEmail)
                .groupName(groupName)
                .creatorEmail(creatorEmail)
                .build();
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
        PendingGroupMembers existingPending = pendingRepo.findByEmailAndGroup(email, group);
        if (existingPending == null) {
            pendingRepo.save(PendingGroupMembers.builder()
                    .email(email)
                    .group(group)
                    .invitedAt(LocalDateTime.now())
                    .build());
        }
    }
    private Group saveGroup(GroupRequest groupRequest, User creator) {
        String groupName = groupRequest.getGroupName();
        String description = groupRequest.getDescription();

        Group group = Group.builder()
                .groupName(groupName)
                .description(description)
                .creator(creator)
                .build();

        return groupRepository.save(group);
    }

    private List<Group> getUserGroups(User user) {
        Stream<UserGroup> userGroupsStream = userGroupRepository.findByUserId(user.getId()).stream();

        List<Group> confirmedGroups = userGroupsStream
                .map(UserGroup::getGroup)
                .collect(Collectors.toList());

        Set<Group> allGroups = new HashSet<>(confirmedGroups);
        return new ArrayList<>(allGroups);
    }

    private List<String> getGroupMembersEmails(Group group) {
        Stream<UserGroup> userGroupsStream = group.getUserGroups().stream();

        Stream<User> userStream = userGroupsStream.map(UserGroup::getUser);

        Stream<String> emailStream = userStream.map(User::getEmail);

        List<String> confirmedEmails = emailStream.collect(Collectors.toList());

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
