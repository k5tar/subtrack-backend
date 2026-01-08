package io.github.kitamura.subtrack_api.controller;

import io.github.kitamura.subtrack_api.dto.UserDto;
import io.github.kitamura.subtrack_api.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;


/**
 * REST controller for user management operations.
 * <p>
 * Provides endpoints for user registration, retrieval, and soft deletion.
 * <p>
 * All responses use UserDto for data transfer.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    /**
     * Register a new user.
     * @param request CreateUserRequest containing email, name, and password
     * @return ResponseEntity with created UserDto and location header
     */
    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid CreateUserRequest request) {
        UserDto created = userService.createUser(request.getEmail(), request.getName(), request.getPassword());
        return ResponseEntity.created(URI.create("/api/users/" + created.getId()))
                .body(created);
    }


    /**
     * Retrieve all active users (excluding soft-deleted).
     * @return ResponseEntity with list of UserDto
     */
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllActiveUsers();
        return ResponseEntity.ok(users);
    }


    /**
     * Retrieve a specific active user by ID.
     * @param id User ID
     * @return ResponseEntity with UserDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") Long id) {
        UserDto user = userService.getActiveUserById(id);
        return ResponseEntity.ok(user);
    }


    /**
     * Soft delete a user by ID.
     * @param id User ID
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // =====================================================
    // DTO: ユーザー登録用
    // =====================================================
    @Data
    public static class CreateUserRequest {
        @NotBlank
        @Email
        private String email;

        @NotBlank
        private String name;

        @NotBlank
        private String password;
    }
}