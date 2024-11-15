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
    @PostMapping("/create")
    public ResponseEntity<CustomResponseBody<GroupResponse>> createGroup(@RequestBody GroupRequest groupRequest) {
        log.info("line number 26 from groupController:{}", groupRequest);

        try {
            GroupResponse response = groupService.createGroup(groupRequest);
            log.info("User registered successfully with email: {}", response.getGroupName());
            CustomResponseBody<GroupResponse> responseBody =new CustomResponseBody<>(CustomResponseBody.Result.SUCCESS,response,"group created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
        }catch (GroupAlreadyExistsException e){
            log.error("Unexpected error during user registration: {}", e.getMessage());
            CustomResponseBody<GroupResponse> responseBody = new CustomResponseBody<>(CustomResponseBody.Result.FAILURE, null, "Something went wrong");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseBody);
        }
        catch (Exception e) {
            log.error("Unexpected error during user registration: {}", e.getMessage());
            CustomResponseBody<GroupResponse> responseBody = new CustomResponseBody<>(CustomResponseBody.Result.FAILURE, null, "Something went wrong");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        }
    }
    @GetMapping("/allGroup")
    public ResponseEntity<CustomResponseBody<List<GroupSummaryResponse>>> getAllGroups() {
        try {
            List<GroupSummaryResponse> groupResponses = groupService.getAllGroups();
            log.info("line 49:{}",groupResponses.toString());
            CustomResponseBody<List<GroupSummaryResponse>> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.SUCCESS,
                    groupResponses,
                    "Fetched all groups successfully"
            );
            return ResponseEntity.status(HttpStatus.OK).body(responseBody);
        } catch (Exception e) {
            log.error("Unexpected error during fetching all groups: {}", e.getMessage());
            CustomResponseBody<List<GroupSummaryResponse>> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    "Something went wrong"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        }
    }

    @GetMapping("/individual")
    public ResponseEntity<CustomResponseBody<GroupResponse>> getIndividualGroup(@RequestParam Integer groupId) {
        try{
            GroupResponse groupResponse = groupService.getGroupById(groupId);
            CustomResponseBody<GroupResponse> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.SUCCESS,
                    groupResponse,
                    "Fetched group details successfully"
            );
            return ResponseEntity.status(HttpStatus.OK).body(responseBody);
        }catch (Exception e) {
            log.error("Unexpected error during group data fetching: {}", e.getMessage());

            CustomResponseBody<GroupResponse> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    "Something went wrong"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        }
    }
    @PostMapping("/addNewMember")
    public ResponseEntity<CustomResponseBody<GroupResponse>> addNewMember(@RequestParam Integer groupId, @RequestBody AddNewMemberRequest request) {
        try{
            GroupResponse response = groupService.addGroupMembers(groupId, request.getNewMemberEmails());
            CustomResponseBody<GroupResponse> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.SUCCESS,
                    response,
                    "Members added successfully"
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/removeMember")
    public ResponseEntity<CustomResponseBody<GroupResponse>> removeMember(
            @RequestParam Integer groupId,
            @RequestParam String memberEmail) {

        try {
            log.info("group:{}", groupId);
            log.info("member:{}", memberEmail);
            GroupResponse response = groupService.removeGroupMember(groupId, memberEmail);
            log.info("User deleted successfully from group: {}, member email: {}", groupId, response.getMembers());

            CustomResponseBody<GroupResponse> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.SUCCESS,
                    response,
                    "Group member deleted successfully"
            );
            return ResponseEntity.status(HttpStatus.OK).body(responseBody);

        }catch(AccessDeniedException e){
            log.error("Access denied exception: {}", e.getMessage());
            CustomResponseBody<GroupResponse> responseBody = new CustomResponseBody<>(CustomResponseBody.Result.FAILURE, null, "Something went wrong");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
        }
        catch (Exception e) {
            log.error("Unexpected error during member deletion: {}", e.getMessage());

            CustomResponseBody<GroupResponse> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    "Something went wrong"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        }
    }
    @GetMapping("/allMembers")
    public ResponseEntity<CustomResponseBody<List<String>>> getAllMembers(@RequestParam Integer groupId) {
        try {
            List<String> members = groupService.allMemberEmails(groupId);
            CustomResponseBody<List<String>> responseBody = new CustomResponseBody<>(CustomResponseBody.Result.SUCCESS,members,"all members successfully fetched");
            return ResponseEntity.status(HttpStatus.OK).body(responseBody);
        }catch (Exception e) {
            log.error("Unexpected error during fetching all members: {}", e.getMessage());
            CustomResponseBody<List<String>> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    "Something went wrong"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        }

    }
    @PutMapping("/update")
    public ResponseEntity<CustomResponseBody<GroupResponse>> updateGroup(
            @RequestParam Integer groupId,
            @RequestBody GroupUpdateRequest updateRequest) {
        try {
            GroupResponse response = groupService.updateGroup(groupId, updateRequest);
            CustomResponseBody<GroupResponse> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.SUCCESS,
                    response,
                    "Group information updated successfully"
            );
            return ResponseEntity.status(HttpStatus.OK).body(responseBody);
        } catch (Exception e) {
            log.error("Unexpected error during group update: {}", e.getMessage());
            CustomResponseBody<GroupResponse> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    "Something went wrong"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        }
    }
    @GetMapping("/pendingRequests")
    public ResponseEntity<CustomResponseBody<List<PendingGroupMemberResponse>>> getAllPendingRequests(
            ) {
        try {
            List<PendingGroupMemberResponse> pendingRequests = groupService.getAllPendingRequest();
            CustomResponseBody<List<PendingGroupMemberResponse>> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.SUCCESS,
                    pendingRequests,
                    "Pending requests fetched successfully."
            );
            return ResponseEntity.status(HttpStatus.OK).body(responseBody);
        } catch (Exception e) {
            CustomResponseBody<List<PendingGroupMemberResponse>> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    "Failed to fetch pending requests."
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        }
    }

    @DeleteMapping("/reject")
    public ResponseEntity<CustomResponseBody<Void>> rejectPendingMember(
            @RequestParam Integer groupId) {
        try {
            groupService.rejectPendingMember(groupId);
            CustomResponseBody<Void> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.SUCCESS,
                    null,
                    "Pending group membership rejected successfully."
            );
            return ResponseEntity.status(HttpStatus.OK).body(responseBody);
        } catch (Exception e) {
            CustomResponseBody<Void> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    "Failed to reject pending group membership."
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        }
    }

    @PostMapping("/accept")
    public ResponseEntity<CustomResponseBody<Void>> acceptPendingMember(
            @RequestParam Integer groupId) {
        try {
            groupService.acceptPendingMember(groupId);
            CustomResponseBody<Void> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.SUCCESS,
                    null,
                    "Pending group membership accepted successfully."
            );
            return ResponseEntity.status(HttpStatus.OK).body(responseBody);
        } catch (Exception e) {
            CustomResponseBody<Void> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    "Failed to accept pending group membership."
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        }
    }

}
