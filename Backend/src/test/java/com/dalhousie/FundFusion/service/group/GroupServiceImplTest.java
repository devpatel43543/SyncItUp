package com.dalhousie.FundFusion.service.group;

import com.dalhousie.FundFusion.exception.*;
import com.dalhousie.FundFusion.group.entity.Group;
import com.dalhousie.FundFusion.group.entity.UserGroup;
import com.dalhousie.FundFusion.group.repository.GroupRepository;
import com.dalhousie.FundFusion.group.repository.PendingGroupMembersRepository;
import com.dalhousie.FundFusion.group.repository.UserGroupRepository;
import com.dalhousie.FundFusion.group.requestEntity.GroupRequest;
import com.dalhousie.FundFusion.group.requestEntity.GroupUpdateRequest;
import com.dalhousie.FundFusion.group.responseEntity.GroupResponse;
import com.dalhousie.FundFusion.group.service.GroupServiceImpl;
import com.dalhousie.FundFusion.user.entity.User;
import com.dalhousie.FundFusion.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;


import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GroupServiceImplTest {

    private static final int MEMBER_ID = 2; // Added constant for the magic number

    @InjectMocks
    private GroupServiceImpl groupService;

    @Mock
    private PendingGroupMembersRepository pendingRepo;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserGroupRepository userGroupRepository;

    @Mock
    private PendingGroupMembersRepository pendingGroupMembersRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private UserDetails userDetails;

    private User user;
    private Group group;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setEmail("creator@example.com");

        group = new Group();
        group.setId(1);
        group.setGroupName("Test Group");
        group.setCreator(user);  // Ensure creator is set

        group.setUserGroups(new ArrayList<>()); // Initialize userGroups to avoid null

        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(userDetails);
        lenient().when(userDetails.getUsername()).thenReturn("creator@example.com");

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testCreateGroup_Success() {

        GroupRequest request = new GroupRequest("Test Group", "Description", List.of("member@example.com"));

        User creator = new User();
        creator.setId(1);
        creator.setEmail("creator@example.com");

        Group group = new Group();
        group.setId(1);
        group.setGroupName("Test Group");

        lenient().when(userRepository.findByEmail("creator@example.com")).thenReturn(Optional.of(creator));
        lenient().when(groupRepository.existsByGroupName("Test Group")).thenReturn(false);
        lenient().when(groupRepository.save(any(Group.class))).thenReturn(group);
        lenient().when(userGroupRepository.findByUserIdAndGroupId(creator.getId(), group.getId())).thenReturn(Optional.empty());

        User member = new User();
        member.setId(MEMBER_ID);
        member.setEmail("member@example.com");

        lenient().when(userRepository.findByEmail("member@example.com")).thenReturn(Optional.of(member));
        lenient().when(userGroupRepository.findByUserIdAndGroupId(member.getId(), group.getId())).thenReturn(Optional.empty());

        GroupResponse response = groupService.createGroup(request);

        assertEquals("Test Group", response.getGroupName());
        assertTrue(response.getMembers().contains("creator@example.com"));
        assertTrue(response.getMembers().contains("member@example.com"));

        verify(groupRepository, times(1)).save(any(Group.class));
    }

    @Test
    public void testAddGroupMembers_Success() {

        List<String> newMembers = List.of("newmember@example.com");
        String creatorEmail = "creator@example.com";

        User creator = new User();
        creator.setEmail(creatorEmail);

        Group group = new Group();
        group.setGroupName("Test Group");
        group.setDescription("Description");
        group.setCreator(creator);

        UserGroup creatorUserGroup = new UserGroup();
        creatorUserGroup.setUser(creator);
        creatorUserGroup.setGroup(group);

        User newMember = new User();
        newMember.setEmail("newmember@example.com");
        UserGroup newUserGroup = new UserGroup();
        newUserGroup.setUser(newMember);
        newUserGroup.setGroup(group);

        List<UserGroup> existingUserGroups = new ArrayList<>();
        existingUserGroups.add(creatorUserGroup);
        existingUserGroups.add(newUserGroup);
        group.setUserGroups(existingUserGroups);

        when(groupRepository.findById(1)).thenReturn(Optional.of(group));
        when(userRepository.findByEmail("newmember@example.com")).thenReturn(Optional.of(newMember));

        GroupResponse response = groupService.addGroupMembers(1, newMembers);

        assertEquals("Test Group", response.getGroupName());
        assertTrue(response.getMembers().contains("creator@example.com"));
        assertTrue(response.getMembers().contains("newmember@example.com"));

        verify(userGroupRepository, times(1)).findByUserIdAndGroupId(newMember.getId(), group.getId());
        verify(groupRepository, times(1)).findById(1);
    }

    @Test
    public void testRemoveGroupMember_Success() {

        String groupName = "Test Group";
        String memberEmail = "member@example.com";
        String creatorEmail = "creator@example.com";

        User creator = new User();
        creator.setId(1);
        creator.setEmail(creatorEmail);

        User member = new User();
        member.setId(MEMBER_ID);
        member.setEmail(memberEmail);

        Group group = new Group();
        group.setId(1);
        group.setGroupName(groupName);
        group.setCreator(creator);

        UserGroup memberUserGroup = new UserGroup();
        memberUserGroup.setUser(member);
        memberUserGroup.setGroup(group);

        List<UserGroup> userGroups = new ArrayList<>(List.of(memberUserGroup));
        group.setUserGroups(userGroups);

        userGroups.remove(memberUserGroup);

        when(groupRepository.findById(1)).thenReturn(Optional.of(group));
        when(userGroupRepository.findByUserEmailAndGroupId(memberEmail, 1)).thenReturn(Optional.of(memberUserGroup));

        GroupResponse response = groupService.removeGroupMember(1, memberEmail);

        assertEquals(groupName, response.getGroupName());
        assertFalse(response.getMembers().contains(memberEmail));

        verify(userGroupRepository, times(1)).delete(memberUserGroup);
    }

    @Test
    public void testRemoveGroupMember_NotExist() {
        when(groupRepository.findById(1)).thenReturn(Optional.of(group));
        lenient().when(userGroupRepository.findByUserEmailAndGroupId("nonmember@example.com", 1))
                .thenReturn(Optional.empty());
        lenient().when(pendingGroupMembersRepository.findByEmailAndGroupId("nonmember@example.com", 1))
                .thenReturn(Optional.empty());

        assertThrows(Throwable.class, () -> groupService.removeGroupMember(1, "nonmember@example.com"));
    }

    @Test
    public void testUpdateGroup_Success() {

        Group group = new Group();
        group.setId(1);
        group.setGroupName("Test Group");

        User creator = new User();
        creator.setEmail("creator@example.com");
        group.setCreator(creator);

        group.setUserGroups(new ArrayList<>());

        GroupUpdateRequest updateRequest = new GroupUpdateRequest("Updated Group", "New description");

        when(groupRepository.findById(1)).thenReturn(Optional.of(group));
        when(groupRepository.save(any(Group.class))).thenReturn(group);

        GroupResponse response = groupService.updateGroup(1, updateRequest);

        assertEquals("Updated Group", response.getGroupName());
        assertEquals("New description", response.getDescription());
    }

    @Test
    public void testGetAllGroups_Success() {

        Group group = new Group();
        group.setId(1);
        group.setGroupName("Test Group");

        User creator = new User();
        creator.setEmail("creator@example.com");
        group.setCreator(creator);

        group.setUserGroups(new ArrayList<>());

        lenient().when(userRepository.findByEmail("creator@example.com")).thenReturn(Optional.of(user));

        UserGroup userGroup = new UserGroup(1, "member@example.com", user, group);
        List<UserGroup> userGroupList = List.of(userGroup);
        lenient().when(userGroupRepository.findByUserId(user.getId())).thenReturn(userGroupList);

        lenient().when(pendingGroupMembersRepository.findByEmail(user.getEmail())).thenReturn(List.of());

        var response = groupService.getAllGroups();

        assertFalse(response.isEmpty());
        assertEquals("Test Group", response.get(0).getGroupName());
    }

    @Test
    public void testGetGroupById_Success() {

        Group group = new Group();
        group.setId(1);
        group.setGroupName("Test Group");

        User creator = new User();
        creator.setEmail("creator@example.com");
        group.setCreator(creator);

        group.setUserGroups(new ArrayList<>());

        when(groupRepository.findById(1)).thenReturn(Optional.of(group));

        GroupResponse response = groupService.getGroupById(1);

        assertEquals("Test Group", response.getGroupName());
        assertEquals("creator@example.com", response.getCreatorEmail());
    }

    @Test
    public void testCreateGroup_EmptyGroupName() {
        GroupRequest request = new GroupRequest("", "Description", List.of("member@example.com"));
        assertThrows(RuntimeException.class, () -> groupService.createGroup(request));
    }

    @Test
    public void testCreateGroup_NullFields() {
        GroupRequest request = new GroupRequest(null, null, null);
        assertThrows(RuntimeException.class, () -> groupService.createGroup(request));
    }

    @Test
    public void testAddGroupMembers_InvalidGroupId() {
        when(groupRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> groupService.addGroupMembers(999, List.of("member@example.com")));
    }

    @Test
    public void testRemoveGroupMember_NotCreator() {
        String creatorEmail = "creator@example.com";
        String currentUserEmail = "member@example.com";

        Group group = new Group();
        group.setId(1);
        group.setGroupName("Test Group");

        User creator = new User();
        creator.setEmail(creatorEmail);
        group.setCreator(creator);

        lenient().when(groupRepository.findById(1)).thenReturn(Optional.of(group));
        lenient().when(userDetails.getUsername()).thenReturn(currentUserEmail);

        assertThrows(AccessDeniedException.class, () -> groupService.removeGroupMember(1, "othermember@example.com"));
    }

    @Test
    public void testUpdateGroup_UnauthorizedUser() {
        String creatorEmail = "creator@example.com";
        String otherEmail = "other@example.com";

        Group group = new Group();
        group.setId(1);
        group.setGroupName("Test Group");

        User creator = new User();
        creator.setEmail(creatorEmail);
        group.setCreator(creator);

        lenient().when(groupRepository.findById(1)).thenReturn(Optional.of(group));
        lenient().when(userDetails.getUsername()).thenReturn(otherEmail);

        GroupUpdateRequest updateRequest = new GroupUpdateRequest("Updated Name", "Updated Description");
        assertThrows(AccessDeniedException.class, () -> groupService.updateGroup(1, updateRequest));
    }

    @Test
    public void testGetGroupById_GroupNotFound() {
        when(groupRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> groupService.getGroupById(999));
    }

//    @Test
//    public void testAcceptPendingMember_NoPendingRequest() {
//        when(pendingGroupMembersRepository.findByEmailAndGroupId("member@example.com", 1)).thenReturn(Optional.empty());
//        assertThrows(MemberNotExist.class, () -> groupService.acceptPendingMember(1));
//    }
//
//    @Test
//    public void testRejectPendingMember_NoPendingRequest() {
//        when(pendingGroupMembersRepository.findByEmailAndGroupId("member@example.com", 1)).thenReturn(Optional.empty());
//        assertThrows(MemberNotExist.class, () -> groupService.rejectPendingMember(1));
//    }

    @Test
    public void testGetAllGroups_NoGroups() {
        when(userRepository.findByEmail("creator@example.com")).thenReturn(Optional.of(user));
        when(userGroupRepository.findByUserId(user.getId())).thenReturn(Collections.emptyList());

        var response = groupService.getAllGroups();
        assertTrue(response.isEmpty());
    }

    @Test
    public void testGetAllPendingRequests_NoRequests() {
        lenient().when(pendingGroupMembersRepository.findByEmail("creator@example.com"))
                .thenReturn(Collections.emptyList());
        var response = groupService.getAllPendingRequest();
        assertEquals(0, response.size());
    }

    @Test
    public void testRemoveGroupMember_RemovingCreator() {
        String creatorEmail = "creator@example.com";

        Group group = new Group();
        group.setId(1);
        group.setGroupName("Test Group");

        User creator = new User();
        creator.setEmail(creatorEmail);
        group.setCreator(creator);

        when(groupRepository.findById(1)).thenReturn(Optional.of(group));

        assertThrows(RuntimeException.class, () -> groupService.removeGroupMember(1, creatorEmail));
    }

    @Test
    public void testGetUserByEmail_UserExists() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        User result = groupService.getUserByEmail("user@example.com");
        assertEquals(user, result);
    }

    @Test
    public void testGetUserByEmail_UserNotFound() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> groupService.getUserByEmail("user@example.com"));
    }

    @Test
    public void testGetUniqueEmails_NonEmptyList() {
        List<String> emails = List.of("email1@example.com", "email2@example.com", "email1@example.com");
        Set<String> uniqueEmails = groupService.getUniqueEmails(emails);
        assertEquals(2, uniqueEmails.size());
        assertTrue(uniqueEmails.contains("email1@example.com"));
    }

    @Test
    public void testGetUniqueEmails_EmptyList() {
        Set<String> uniqueEmails = groupService.getUniqueEmails(Collections.emptyList());
        assertTrue(uniqueEmails.isEmpty());
    }
}
