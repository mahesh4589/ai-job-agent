package com.ai.agent.controller;


import com.ai.agent.dto.ApiResponse;
import com.ai.agent.dto.LoginDto;
import com.ai.agent.dto.Userdto;
import com.ai.agent.entity.User;
import com.ai.agent.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User API", description = "User Management Operations")
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Get User By Id",
            description = "Returns user details by id")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserRecordById(@PathVariable("id") int id) {
        log.info("Fetching user by id: {}", id);
        User res = userService.getUserRecordById(id);

        log.info("User found with id: {}", id);
        return ResponseEntity.ok(
                ApiResponse.success("User Found..", res,
                        HttpStatus.OK.value()));


    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUserRecord() {
        log.info("Fetching all users");
        List<User> responce = userService.getAllUserRecord();

        log.info("Total users fetched: {}", responce.size());
        return ResponseEntity.ok(ApiResponse.
                success("All user record found.. ", responce, HttpStatus.OK.value()));


    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUserRecord(@PathVariable("id") int id, @Valid @RequestBody Userdto user) {
        log.info("Update request received for user id: {}", id);
        User res = userService.updateUserRecord(id, user);
        log.info("User updated successfully with id: {}", id);
        return ResponseEntity.ok(ApiResponse.success("Update the record...", res, HttpStatus.OK.value()));

    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/email/{emailId}")
    public ResponseEntity<ApiResponse<User>> findRecordByEmailId(@PathVariable("emailId") String emailId) {
        log.info("Fetching user by email: {}", emailId);
        User res = userService.findRecordByEmailId(emailId);
        log.info("User found for email: {}", emailId);
        return ResponseEntity.ok(ApiResponse.success("User found successfully.", res, HttpStatus.OK.value()));
    }
    @Operation(
            summary = "Delete User")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteUser(
            @PathVariable Integer id) {
        log.info("Delete request received for user id: {}", id);
        userService.deleteUser(id);
        log.info("User deleted successfully with id: {}", id);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "User deleted successfully",
                        "Success",
                        HttpStatus.OK.value()));
    }


}
