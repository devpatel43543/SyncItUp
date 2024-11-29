package com.dalhousie.FundFusion.group.controller;


import com.dalhousie.FundFusion.exception.AccessDeniedException;
import com.dalhousie.FundFusion.exception.GroupAlreadyExistsException;
import com.dalhousie.FundFusion.group.requestEntity.AddNewMemberRequest;
import com.dalhousie.FundFusion.group.requestEntity.GroupRequest;
import com.dalhousie.FundFusion.group.requestEntity.GroupUpdateRequest;
import com.dalhousie.FundFusion.group.responseEntity.GroupResponse;
import com.dalhousie.FundFusion.group.responseEntity.GroupSummaryResponse;
import com.dalhousie.FundFusion.group.responseEntity.PendingGroupMemberResponse;
import com.dalhousie.FundFusion.group.service.GroupService;
import com.dalhousie.FundFusion.util.CustomResponseBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/group")
public class GroupController {
    private final GroupService groupService;
    private static final String GENERAL_ERROR = "Something went wrong";

    @PostMapping("/create")
    public ResponseEntity<CustomResponseBody<GroupResponse>> createGroup(@RequestBody GroupRequest groupRequest) {
        log.info("line number 26 from groupController:{}", groupRequest);
        try {
            GroupResponse response = groupService.createGroup(groupRequest);
            log.info("User registered successfully with email: {}", response.getGroupName());
            return buildResponse(HttpStatus.CREATED, CustomResponseBody.Result.SUCCESS, response, "Group created successfully");
        } catch (GroupAlreadyExistsException e) {
            log.error("Group creation error: {}", e.getMessage());
            return buildResponse(HttpStatus.CONFLICT, CustomResponseBody.Result.FAILURE, null, "Group already exists");
        } catch (Exception e) {
            log.error("Unexpected error during group creation: {}", e.getMessage());
            return buildErrorResponse();
        }
    }

    @GetMapping("/allGroup")
    public ResponseEntity<CustomResponseBody<List<GroupSummaryResponse>>> getAllGroups() {
        try {
            List<GroupSummaryResponse> groupResponses = groupService.getAllGroups();
            log.info("line 49: {}", groupResponses.toString());
            return buildResponse(HttpStatus.OK, CustomResponseBody.Result.SUCCESS, groupResponses, "Fetched all groups successfully");
        } catch (Exception e) {
            log.error("Unexpected error during fetching all groups: {}", e.getMessage());
            return buildErrorResponse();
        }
    }

    @GetMapping("/individual")
    public ResponseEntity<CustomResponseBody<GroupResponse>> getIndividualGroup(@RequestParam Integer groupId) {
        try {
            GroupResponse groupResponse = groupService.getGroupById(groupId);
            return buildResponse(HttpStatus.OK, CustomResponseBody.Result.SUCCESS, groupResponse, "group details successfully");
        } catch (Exception e) {
            log.error("Unexpected error during group data fetching: {}", e.getMessage());
            return buildErrorResponse();
        }
    }

    @PostMapping("/addNewMember")
    public ResponseEntity<CustomResponseBody<GroupResponse>> addNewMember(
            @RequestParam Integer groupId,
            @RequestBody AddNewMemberRequest request) {
        try {
            GroupResponse response = groupService.addGroupMembers(groupId, request.getNewMemberEmails());
            return buildResponse(HttpStatus.CREATED, CustomResponseBody.Result.SUCCESS, response, "Members added successfully");
        } catch (Exception e) {
            log.error("Unexpected error during adding new members: {}", e.getMessage());
            return buildErrorResponse();
        }
    }


    @DeleteMapping("/removeMember")
    public ResponseEntity<CustomResponseBody<GroupResponse>> removeMember(
            @RequestParam Integer groupId,
            @RequestParam String memberEmail) {
        try {
            log.info("Removing member: group={}, member={}", groupId, memberEmail);
            GroupResponse response = groupService.removeGroupMember(groupId, memberEmail);
            return buildResponse(HttpStatus.OK, CustomResponseBody.Result.SUCCESS, response, "Group member deleted successfully");
        } catch (AccessDeniedException e) {
            log.error("Access denied: {}", e.getMessage());
            return buildResponse(HttpStatus.UNAUTHORIZED, CustomResponseBody.Result.FAILURE, null, "Access denied");
        } catch (Exception e) {
            log.error("Unexpected error during member deletion: {}", e.getMessage());
            return buildErrorResponse();
        }
    }

    @GetMapping("/allMembers")
    public ResponseEntity<CustomResponseBody<List<String>>> getAllMembers(@RequestParam Integer groupId) {
        try {
            List<String> members = groupService.allMemberEmails(groupId);
            return buildResponse(
                    HttpStatus.OK,
                    CustomResponseBody.Result.SUCCESS,
                    members,
                    "All members successfully fetched"
            );
        } catch (Exception e) {
            log.error("Unexpected error during fetching all members: {}", e.getMessage());
            return buildErrorResponse();

        }
    }

    @PutMapping("/update")
    public ResponseEntity<CustomResponseBody<GroupResponse>> updateGroup(
            @RequestParam Integer groupId,
            @RequestBody GroupUpdateRequest updateRequest) {
        try {
            GroupResponse response = groupService.updateGroup(groupId, updateRequest);
            return buildResponse(
                    HttpStatus.OK,
                    CustomResponseBody.Result.SUCCESS,
                    response,
                    "Group information updated successfully"
            );
        } catch (Exception e) {
            log.error("Unexpected error during group update: {}", e.getMessage());
            return buildErrorResponse();

        }
    }

    @GetMapping("/pendingRequests")
    public ResponseEntity<CustomResponseBody<List<PendingGroupMemberResponse>>> getAllPendingRequests() {
        try {
            List<PendingGroupMemberResponse> pendingRequests = groupService.getAllPendingRequest();
            return buildResponse(HttpStatus.OK, CustomResponseBody.Result.SUCCESS, pendingRequests, "fetched successfully.");
        } catch (Exception e) {
            log.error("Error fetching pending requests: {}", e.getMessage());
            return buildErrorResponse();
        }
    }


    @DeleteMapping("/reject")
    public ResponseEntity<CustomResponseBody<Void>> rejectPendingMember(@RequestParam Integer groupId) {
        try {
            groupService.rejectPendingMember(groupId);
            return buildResponse(HttpStatus.OK, CustomResponseBody.Result.SUCCESS, null, "rejected successfully.");
        } catch (Exception e) {
            log.error("Error rejecting pending member: {}", e.getMessage());
            return buildErrorResponse();
        }
    }


    @PostMapping("/accept")
    public ResponseEntity<CustomResponseBody<Void>> acceptPendingMember(@RequestParam Integer groupId) {
        try {
            groupService.acceptPendingMember(groupId);
            return buildResponse(HttpStatus.OK, CustomResponseBody.Result.SUCCESS, null, "membership accepted successfully.");
        } catch (Exception e) {
            log.error("Error accepting pending member: {}", e.getMessage());
            return buildErrorResponse();
        }
    }


    // Generic helper method to create a response
    private <T> ResponseEntity<CustomResponseBody<T>> buildResponse(
            HttpStatus status,
            CustomResponseBody.Result result,
            T data,
            String message) {
        CustomResponseBody<T> responseBody = new CustomResponseBody<>(result, data, message);
        return ResponseEntity.status(status).body(responseBody);
    }

    private <T> ResponseEntity<CustomResponseBody<T>> buildErrorResponse() {
        return buildResponse(HttpStatus.BAD_REQUEST, CustomResponseBody.Result.FAILURE, null, GENERAL_ERROR);
    }

}
