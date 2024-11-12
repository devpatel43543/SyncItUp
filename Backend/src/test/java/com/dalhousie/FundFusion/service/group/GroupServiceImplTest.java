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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GroupServiceImplTest {

    @InjectMocks
    private GroupServiceImpl groupService;

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

        when(userRepository.findByEmail("creator@example.com")).thenReturn(Optional.of(creator));
        when(groupRepository.existsByGroupName("Test Group")).thenReturn(false);
        when(groupRepository.save(any(Group.class))).thenReturn(group);
        when(userGroupRepository.findByUserIdAndGroupId(creator.getId(), group.getId())).thenReturn(Optional.empty());

        User member = new User();
        member.setId(2);
        member.setEmail("member@example.com");
        when(userRepository.findByEmail("member@example.com")).thenReturn(Optional.of(member));
        when(userGroupRepository.findByUserIdAndGroupId(member.getId(), group.getId())).thenReturn(Optional.empty());

        GroupResponse response = groupService.createGroup(request);

        assertEquals("Test Group", response.getGroupName());
        assertTrue(response.getMembers().contains("creator@example.com"));
        assertTrue(response.getMembers().contains("member@example.com"));

        verify(groupRepository, times(1)).existsByGroupName("Test Group");
        verify(groupRepository, times(1)).save(any(Group.class));
    }


    @Test
    public void testCreateGroup_GroupAlreadyExists() {

        GroupRequest request = new GroupRequest("Test Group", "Description", List.of("member@example.com"));

        when(userRepository.findByEmail("creator@example.com")).thenReturn(Optional.of(user));
        when(groupRepository.existsByGroupName("Test Group")).thenReturn(true);

        assertThrows(GroupAlreadyExistsException.class, () -> groupService.createGroup(request));
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

        verify(userGroupRepository, times(1)).save(any(UserGroup.class)); // Confirms new member addition
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
        member.setId(2);
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
        when(userGroupRepository.findByUserEmailAndGroupId("nonmember@example.com", 1)).thenReturn(Optional.empty());
        when(pendingGroupMembersRepository.findByEmailAndGroupId("nonmember@example.com", 1)).thenReturn(Optional.empty());

        assertThrows(MemberNotExist.class, () -> groupService.removeGroupMember(1, "nonmember@example.com"));
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

        when(userRepository.findByEmail("creator@example.com")).thenReturn(Optional.of(user));
        when(userGroupRepository.findByUserId(user.getId())).thenReturn(List.of(new UserGroup(1, "member@example.com", user, group)));
        when(pendingGroupMembersRepository.findByEmail(user.getEmail())).thenReturn(List.of());

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
}
