package io.github.kitamura.subtrack_api.controller;

import io.github.kitamura.subtrack_api.dto.SubscriptionHistoryResponseDto;
import io.github.kitamura.subtrack_api.dto.SubscriptionRequestDto;
import io.github.kitamura.subtrack_api.dto.SubscriptionResponseDto;
import io.github.kitamura.subtrack_api.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private static final String USER_ID_HEADER = "X-User-Id";

    // =====================================================
    // 全サブスクリプション取得（削除済み除外）
    // =====================================================
    @GetMapping
    public ResponseEntity<List<SubscriptionResponseDto>> getAll(
            @RequestHeader(USER_ID_HEADER) Long userId) {
        List<SubscriptionResponseDto> subscriptions = subscriptionService.findAllByUserId(userId);
        return ResponseEntity.ok(subscriptions);
    }

    // =====================================================
    // 登録
    // =====================================================
    @PostMapping
    public ResponseEntity<SubscriptionHistoryResponseDto> create(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @Valid @RequestBody SubscriptionRequestDto request) {
        SubscriptionHistoryResponseDto created = subscriptionService.create(userId, request);
        return ResponseEntity.created(URI.create("/api/subscriptions/" + created.getSubscriptionId()))
                .body(created);
    }

    // =====================================================
    // 更新
    // =====================================================
    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionHistoryResponseDto> update(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable("id") Long id,
            @Valid @RequestBody SubscriptionRequestDto request) {
        SubscriptionHistoryResponseDto updated = subscriptionService.update(userId, id, request);
        return ResponseEntity.ok(updated);
    }

    // =====================================================
    // 論理削除
    // =====================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<SubscriptionHistoryResponseDto> delete(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable("id") Long id) {
        SubscriptionHistoryResponseDto deleted = subscriptionService.delete(userId, id);
        return ResponseEntity.ok(deleted);
    }

    // =====================================================
    // 履歴取得
    // =====================================================
    @GetMapping("/history")
    public ResponseEntity<List<SubscriptionHistoryResponseDto>> getHistory(
            @RequestHeader(USER_ID_HEADER) Long userId) {
        List<SubscriptionHistoryResponseDto> history = subscriptionService.getHistoryByUserId(userId);
        return ResponseEntity.ok(history);
    }
}