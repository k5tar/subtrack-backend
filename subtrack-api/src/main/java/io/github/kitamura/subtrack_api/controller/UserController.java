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

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // =====================================================
    // ユーザー登録
    // =====================================================
    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid CreateUserRequest request) {
        UserDto created = userService.createUser(request.getEmail(), request.getName(), request.getPassword());
        return ResponseEntity.created(URI.create("/api/users/" + created.getId()))
                .body(created);
    }

    // =====================================================
    // 全ユーザー取得（論理削除除外）
    // =====================================================
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllActiveUsers();
        return ResponseEntity.ok(users);
    }

    // =====================================================
    // 特定ユーザー取得（論理削除除外）
    // =====================================================
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") Long id) {
        UserDto user = userService.getActiveUserById(id);
        return ResponseEntity.ok(user);
    }

    // =====================================================
    // ユーザー削除（論理削除）
    // =====================================================
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