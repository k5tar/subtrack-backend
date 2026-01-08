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


/**
 * REST controller for subscription management operations.
 * <p>
 * Provides endpoints for creating, updating, retrieving, and soft-deleting subscriptions, as well as viewing change history.
 * <p>
 * All endpoints require the X-User-Id header to identify the user.
 */
@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private static final String USER_ID_HEADER = "X-User-Id";


    /**
     * Retrieve all active subscriptions for a user.
     * @param userId User ID from X-User-Id header
     * @return ResponseEntity with list of SubscriptionResponseDto
     */
    @GetMapping
    public ResponseEntity<List<SubscriptionResponseDto>> getAll(
            @RequestHeader(USER_ID_HEADER) Long userId) {
        List<SubscriptionResponseDto> subscriptions = subscriptionService.findAllByUserId(userId);
        return ResponseEntity.ok(subscriptions);
    }


        /**
         * Create a new subscription for a user.
         * @param userId User ID from X-User-Id header
         * @param request SubscriptionRequestDto with subscription details
         * @return ResponseEntity with created SubscriptionHistoryResponseDto
         */
        @PostMapping
        public ResponseEntity<SubscriptionHistoryResponseDto> create(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @Valid @RequestBody SubscriptionRequestDto request) {
        SubscriptionHistoryResponseDto created = subscriptionService.create(userId, request);
        return ResponseEntity.created(URI.create("/api/subscriptions/" + created.getSubscriptionId()))
            .body(created);
        }


    /**
     * Update an existing subscription for a user.
     * @param userId User ID from X-User-Id header
     * @param id Subscription ID
     * @param request SubscriptionRequestDto with updated details
     * @return ResponseEntity with updated SubscriptionHistoryResponseDto
     */
    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionHistoryResponseDto> update(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable("id") Long id,
            @Valid @RequestBody SubscriptionRequestDto request) {
        SubscriptionHistoryResponseDto updated = subscriptionService.update(userId, id, request);
        return ResponseEntity.ok(updated);
    }

    // =====================================================
    // Soft delete subscription (logical deletion)
    // =====================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<SubscriptionHistoryResponseDto> delete(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable("id") Long id) {
        SubscriptionHistoryResponseDto deleted = subscriptionService.delete(userId, id);
        return ResponseEntity.ok(deleted);
    }

    // =====================================================
    // Retrieve subscription change history
    // =====================================================
    @GetMapping("/history")
    public ResponseEntity<List<SubscriptionHistoryResponseDto>> getHistory(
            @RequestHeader(USER_ID_HEADER) Long userId) {
        List<SubscriptionHistoryResponseDto> history = subscriptionService.getHistoryByUserId(userId);
        return ResponseEntity.ok(history);
    }
}