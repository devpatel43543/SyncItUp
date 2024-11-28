package com.dalhousie.FundFusion.controller.group;

import com.dalhousie.FundFusion.exception.AccessDeniedException;
import com.dalhousie.FundFusion.exception.GroupAlreadyExistsException;
import com.dalhousie.FundFusion.group.controller.GroupController;
import com.dalhousie.FundFusion.group.requestEntity.AddNewMemberRequest;
import com.dalhousie.FundFusion.group.requestEntity.GroupRequest;
import com.dalhousie.FundFusion.group.requestEntity.GroupUpdateRequest;
import com.dalhousie.FundFusion.group.responseEntity.GroupResponse;
import com.dalhousie.FundFusion.group.responseEntity.GroupSummaryResponse;
import com.dalhousie.FundFusion.group.service.GroupService;
import com.dalhousie.FundFusion.util.CustomResponseBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.NoSuchElementException;

class GroupControllerTest {

    @Test
    void testCreateGroup_Success() {

        GroupService mockService = Mockito.mock(GroupService.class);

        GroupResponse mockGroupResponse = GroupResponse.builder()
                .groupName("Group 1")
                .description("This is Group 1")
                .creatorEmail("creator@example.com")
                .members(List.of("member1@example.com", "member2@example.com"))
                .build();

        Mockito.when(mockService.createGroup(Mockito.any(GroupRequest.class)))
                .thenReturn(mockGroupResponse);

        GroupController controller = new GroupController(mockService);

        GroupRequest groupRequest = new GroupRequest();
        groupRequest.setGroupName("Group 1");

        ResponseEntity<CustomResponseBody<GroupResponse>> response = controller.createGroup(groupRequest);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertEquals("Group created successfully", response.getBody().message());
        GroupResponse responseData = response.getBody().data();
        Assertions.assertEquals("Group 1", responseData.getGroupName());
        Assertions.assertEquals("This is Group 1", responseData.getDescription());
        Assertions.assertEquals("creator@example.com", responseData.getCreatorEmail());
        Assertions.assertEquals(2, responseData.getMembers().size());
        Assertions.assertTrue(responseData.getMembers().contains("member1@example.com"));
        Assertions.assertTrue(responseData.getMembers().contains("member2@example.com"));
    }

    @Test
    void testCreateGroup_GroupAlreadyExists() {

        GroupService mockService = Mockito.mock(GroupService.class);

        Mockito.when(mockService.createGroup(Mockito.any(GroupRequest.class)))
                .thenThrow(new GroupAlreadyExistsException("Group already exists"));

        GroupController controller = new GroupController(mockService);

        GroupRequest groupRequest = new GroupRequest();
        groupRequest.setGroupName("Group 1");

        ResponseEntity<CustomResponseBody<GroupResponse>> response = controller.createGroup(groupRequest);

        Assertions.assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("Group already exists", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testGetAllGroups_Success() {

        GroupService mockService = Mockito.mock(GroupService.class);

        List<GroupSummaryResponse> mockGroups = List.of(
                GroupSummaryResponse.builder()
                        .groupId(1)
                        .groupName("Group 1")
                        .description("This is Group 1")
                        .creatorEmail("creator1@example.com")
                        .members(List.of("member1@example.com", "member2@example.com"))
                        .build(),
                GroupSummaryResponse.builder()
                        .groupId(2)
                        .groupName("Group 2")
                        .description("This is Group 2")
                        .creatorEmail("creator2@example.com")
                        .members(List.of("member3@example.com"))
                        .build()
        );

        Mockito.when(mockService.getAllGroups()).thenReturn(mockGroups);

        GroupController controller = new GroupController(mockService);

        ResponseEntity<CustomResponseBody<List<GroupSummaryResponse>>> response = controller.getAllGroups();

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertEquals("Fetched all groups successfully", response.getBody().message());

        List<GroupSummaryResponse> responseData = response.getBody().data();
        Assertions.assertNotNull(responseData);
        Assertions.assertEquals(2, responseData.size());

        GroupSummaryResponse group1 = responseData.get(0);
        Assertions.assertEquals(1, group1.getGroupId());
        Assertions.assertEquals("Group 1", group1.getGroupName());
        Assertions.assertEquals("This is Group 1", group1.getDescription());
        Assertions.assertEquals("creator1@example.com", group1.getCreatorEmail());
        Assertions.assertEquals(2, group1.getMembers().size());
        Assertions.assertTrue(group1.getMembers().contains("member1@example.com"));
        Assertions.assertTrue(group1.getMembers().contains("member2@example.com"));

        GroupSummaryResponse group2 = responseData.get(1);
        Assertions.assertEquals(2, group2.getGroupId());
        Assertions.assertEquals("Group 2", group2.getGroupName());
        Assertions.assertEquals("This is Group 2", group2.getDescription());
        Assertions.assertEquals("creator2@example.com", group2.getCreatorEmail());
        Assertions.assertEquals(1, group2.getMembers().size());
        Assertions.assertTrue(group2.getMembers().contains("member3@example.com"));
    }

    @Test
    void testAddNewMember_Success() {
        GroupService mockService = Mockito.mock(GroupService.class);

        GroupResponse mockGroupResponse = GroupResponse.builder()
                .groupName("Group 1")
                .description("This is Group 1")
                .creatorEmail("creator@example.com")
                .members(List.of("member1@example.com", "member2@example.com"))
                .build();

        Mockito.when(mockService.addGroupMembers(Mockito.anyInt(), Mockito.anyList()))
                .thenReturn(mockGroupResponse);

        GroupController controller = new GroupController(mockService);

        AddNewMemberRequest addNewMemberRequest = new AddNewMemberRequest();
        addNewMemberRequest.setNewMemberEmails(List.of("member1@example.com", "member2@example.com"));

        ResponseEntity<CustomResponseBody<GroupResponse>> response =
                controller.addNewMember(1, addNewMemberRequest);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertEquals("Members added successfully", response.getBody().message());
        Assertions.assertEquals("Group 1", response.getBody().data().getGroupName());
    }

    @Test
    void testRemoveMember_AccessDenied() {
        GroupService mockService = Mockito.mock(GroupService.class);

        Mockito.when(mockService.removeGroupMember(Mockito.anyInt(), Mockito.anyString()))
                .thenThrow(new AccessDeniedException("Access denied"));

        GroupController controller = new GroupController(mockService);

        ResponseEntity<CustomResponseBody<GroupResponse>> response = controller.removeMember(1, "member@example.com");

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("Access denied", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testRejectPendingMember_Success() {
        GroupService mockService = Mockito.mock(GroupService.class);

        Mockito.doNothing().when(mockService).rejectPendingMember(Mockito.anyInt());

        GroupController controller = new GroupController(mockService);

        ResponseEntity<CustomResponseBody<Void>> response = controller.rejectPendingMember(1);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertEquals("rejected successfully.", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testCreateGroup_InvalidData() {
        GroupService mockService = Mockito.mock(GroupService.class);

        Mockito.when(mockService.createGroup(Mockito.any(GroupRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid group data"));

        GroupController controller = new GroupController(mockService);

        GroupRequest request = new GroupRequest();
        request.setGroupName("");

        ResponseEntity<CustomResponseBody<GroupResponse>> response = controller.createGroup(request);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("Something went wrong", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testGetAllMembers_EmptyGroup() {
        GroupService mockService = Mockito.mock(GroupService.class);

        Mockito.when(mockService.allMemberEmails(Mockito.anyInt())).thenReturn(List.of());

        GroupController controller = new GroupController(mockService);

        ResponseEntity<CustomResponseBody<List<String>>> response = controller.getAllMembers(1);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertEquals("All members successfully fetched", response.getBody().message());
        Assertions.assertTrue(response.getBody().data().isEmpty());
    }

    @Test
    void testRemoveMember_Unauthorized() {
        GroupService mockService = Mockito.mock(GroupService.class);

        Mockito.when(mockService.removeGroupMember(Mockito.anyInt(), Mockito.anyString()))
                .thenThrow(new AccessDeniedException("Access denied"));

        GroupController controller = new GroupController(mockService);

        ResponseEntity<CustomResponseBody<GroupResponse>> response = controller.removeMember(1, "unauthorized@example.com");

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("Access denied", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testAcceptPendingMember_Success() {
        GroupService mockService = Mockito.mock(GroupService.class);
        Mockito.doNothing().when(mockService).acceptPendingMember(Mockito.anyInt());

        GroupController controller = new GroupController(mockService);
        ResponseEntity<CustomResponseBody<Void>> response = controller.acceptPendingMember(1);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
    }

    @Test
    void testGetAllGroups_Failure() {
        GroupService mockService = Mockito.mock(GroupService.class);

        Mockito.when(mockService.getAllGroups())
                .thenThrow(new RuntimeException("Database error"));

        GroupController controller = new GroupController(mockService);

        ResponseEntity<CustomResponseBody<List<GroupSummaryResponse>>> response = controller.getAllGroups();

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("Something went wrong", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testRejectPendingMember_Failure() {
        GroupService mockService = Mockito.mock(GroupService.class);

        Mockito.doThrow(new NoSuchElementException("Pending member not found"))
                .when(mockService).rejectPendingMember(Mockito.anyInt());

        GroupController controller = new GroupController(mockService);

        ResponseEntity<CustomResponseBody<Void>> response = controller.rejectPendingMember(999);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("Something went wrong", response.getBody().message());
    }

    @Test
    void testGetAllMembers_Failure() {
        GroupService mockService = Mockito.mock(GroupService.class);

        Mockito.when(mockService.allMemberEmails(Mockito.anyInt()))
                .thenThrow(new NoSuchElementException("Group not found"));

        GroupController controller = new GroupController(mockService);

        ResponseEntity<CustomResponseBody<List<String>>> response = controller.getAllMembers(1);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("Something went wrong", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }


    @Test
    void testUpdateGroup_Failure() {
        GroupService mockService = Mockito.mock(GroupService.class);

        Mockito.when(mockService.updateGroup(Mockito.anyInt(), Mockito.any(GroupUpdateRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        GroupController controller = new GroupController(mockService);

        GroupUpdateRequest request = new GroupUpdateRequest();
        request.setGroupName("Invalid Group");

        ResponseEntity<CustomResponseBody<GroupResponse>> response = controller.updateGroup(1, request);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("Something went wrong", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testAcceptPendingMember_Failure() {
        GroupService mockService = Mockito.mock(GroupService.class);

        Mockito.doThrow(new IllegalArgumentException("Invalid group ID"))
                .when(mockService).acceptPendingMember(Mockito.anyInt());

        GroupController controller = new GroupController(mockService);

        ResponseEntity<CustomResponseBody<Void>> response = controller.acceptPendingMember(999);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("Something went wrong", response.getBody().message());
    }

    @Test
    void testRemoveMember_Failure() {
        GroupService mockService = Mockito.mock(GroupService.class);

        Mockito.when(mockService.removeGroupMember(Mockito.anyInt(), Mockito.anyString()))
                .thenThrow(new RuntimeException("Unexpected error"));

        GroupController controller = new GroupController(mockService);

        ResponseEntity<CustomResponseBody<GroupResponse>> response = controller.removeMember(1, "invalid@example.com");

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("Something went wrong", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testAddNewMember_Failure() {
        GroupService mockService = Mockito.mock(GroupService.class);

        Mockito.when(mockService.addGroupMembers(Mockito.anyInt(), Mockito.anyList()))
                .thenThrow(new IllegalArgumentException("Invalid member data"));

        GroupController controller = new GroupController(mockService);

        AddNewMemberRequest request = new AddNewMemberRequest();
        request.setNewMemberEmails(List.of("invalid@example.com"));

        ResponseEntity<CustomResponseBody<GroupResponse>> response = controller.addNewMember(1, request);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("Something went wrong", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }


}
