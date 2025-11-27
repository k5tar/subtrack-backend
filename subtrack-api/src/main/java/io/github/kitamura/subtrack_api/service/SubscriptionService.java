package io.github.kitamura.subtrack_api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kitamura.subtrack_api.dto.SubscriptionHistoryResponseDto;
import io.github.kitamura.subtrack_api.dto.SubscriptionRequestDto;
import io.github.kitamura.subtrack_api.dto.SubscriptionResponseDto;
import io.github.kitamura.subtrack_api.entity.Subscription;
import io.github.kitamura.subtrack_api.entity.SubscriptionHistory;
import io.github.kitamura.subtrack_api.entity.User;
import io.github.kitamura.subtrack_api.exception.CustomException;
import io.github.kitamura.subtrack_api.mapper.SubscriptionMapper;
import io.github.kitamura.subtrack_api.repository.SubscriptionHistoryRepository;
import io.github.kitamura.subtrack_api.repository.SubscriptionRepository;
import io.github.kitamura.subtrack_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionHistoryRepository subscriptionHistoryRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    // =====================================================
    // サブスクリプション取得（論理削除除外）
    // =====================================================
    @Transactional(readOnly = true)
    public List<SubscriptionResponseDto> findAllByUserId(Long userId) {
        log.info("[SubscriptionService] Fetching active subscriptions for userId={}", userId);
        List<Subscription> subscriptions = subscriptionRepository.findActiveByUserId(userId);
        return subscriptions.stream()
                .map(SubscriptionMapper::toDto) // toDto must include deleted flag
                .toList();
    }

    // =====================================================
    // 登録
    // =====================================================
    @Transactional
    public SubscriptionHistoryResponseDto create(Long userId, SubscriptionRequestDto request) {
        log.info("[SubscriptionService] Creating subscription for userId={}", userId);

        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new CustomException("User not found or deleted: " + userId));

        // map to entity
        Subscription entity = SubscriptionMapper.toEntity(request, user);
        entity.setDeleted(false);

        try {
            Subscription saved = subscriptionRepository.save(entity);

            // history: previous=null, current=saved
            SubscriptionHistory history = saveHistory(saved, user, null, saved, "INSERT");
            return SubscriptionHistoryResponseDto.of(saved, history);
        } catch (DataIntegrityViolationException e) {
            log.error("[SubscriptionService] DataIntegrityViolation on create userId={}, name={}", userId, request.getName(), e);
            throw new CustomException("Failed to create subscription: possible duplicate or constraint violation", e);
        } catch (Exception e) {
            log.error("[SubscriptionService] Unexpected error on create for userId={}", userId, e);
            throw new CustomException("Failed to create subscription", e);
        }
    }

    // =====================================================
    // 更新（論理削除済みは対象外）
    // =====================================================
    @Transactional
    public SubscriptionHistoryResponseDto update(Long userId, Long subscriptionId, SubscriptionRequestDto request) {
        log.info("[SubscriptionService] Updating subscriptionId={} for userId={}", subscriptionId, userId);

        Subscription existing = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new CustomException("Subscription not found: " + subscriptionId));

        if (existing.isDeleted()) {
            throw new CustomException("Cannot update deleted subscription: " + subscriptionId);
        }

        if (!existing.getUser().getId().equals(userId)) {
            throw new CustomException("Subscription not found for user: " + subscriptionId);
        }

        Subscription before = cloneSubscription(existing);

        SubscriptionMapper.updateEntityFromDto(request, existing);

        try {
            Subscription saved = subscriptionRepository.save(existing);
            SubscriptionHistory history = saveHistory(saved, saved.getUser(), before, saved, "UPDATE");
            return SubscriptionHistoryResponseDto.of(saved, history);
        } catch (DataIntegrityViolationException e) {
            log.error("[SubscriptionService] DataIntegrityViolation on update subscriptionId={}", subscriptionId, e);
            throw new CustomException("Failed to update subscription: constraint violation", e);
        } catch (Exception e) {
            log.error("[SubscriptionService] Unexpected error on update subscriptionId={}", subscriptionId, e);
            throw new CustomException("Failed to update subscription", e);
        }
    }

    // =====================================================
    // 削除（論理削除）
    // =====================================================
    @Transactional
    public SubscriptionHistoryResponseDto delete(Long userId, Long subscriptionId) {
        log.info("[SubscriptionService] Soft deleting subscriptionId={} for userId={}", subscriptionId, userId);

        Subscription existing = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new CustomException("Subscription not found: " + subscriptionId));

        if (existing.isDeleted()) {
            throw new CustomException("Subscription already deleted: " + subscriptionId);
        }

        if (!existing.getUser().getId().equals(userId)) {
            throw new CustomException("Subscription not found for user: " + subscriptionId);
        }

        // snapshot before deletion
        Subscription before = cloneSubscription(existing);

        // mark deleted and persist
        existing.setDeleted(true);
        Subscription saved = subscriptionRepository.save(existing);

        // save history: record before (active state) and after (deleted=true)
        SubscriptionHistory history = saveHistory(saved, saved.getUser(), before, saved, "DELETE");
        return SubscriptionHistoryResponseDto.of(saved, history);
    }

    // =====================================================
    // 履歴取得
    // =====================================================
    @Transactional(readOnly = true)
    public List<SubscriptionHistoryResponseDto> getHistoryByUserId(Long userId) {
        log.info("[SubscriptionService] Fetching subscription history for userId={}", userId);

        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new CustomException("User not found or deleted: " + userId));

        return subscriptionHistoryRepository.findByUserOrderByChangedAtDesc(user)
                .stream()
                .map(SubscriptionHistoryResponseDto::fromHistory)
                .toList();
    }

    // =====================================================
    // 内部処理: 履歴登録
    //  - previous/current は DTO に変換して JSON 化（エンティティのまま直列化しない）
    // =====================================================
    private SubscriptionHistory saveHistory(Subscription subscription, User user,
                                            Subscription previous, Subscription current, String actionType) {
        try {
            JsonNode previousJson = previous != null ?
                    objectMapper.valueToTree(SubscriptionMapper.toDto(previous))
                    : null;
            JsonNode newJson = current != null ?
                    objectMapper.valueToTree(SubscriptionMapper.toDto(current))
                    : null;

            SubscriptionHistory history = SubscriptionHistory.builder()
                    .subscription(subscription)
                    .user(user)
                    .name(subscription.getName())
                    .price(subscription.getPrice())
                    .billingCycle(subscription.getBillingCycle())
                    .category(subscription.getCategory())
                    .actionType(actionType)
                    .previousValue(previousJson)
                    .newValue(newJson)
                    .build();

            SubscriptionHistory savedHistory = subscriptionHistoryRepository.save(history);
            log.debug("[SubscriptionService] History saved: subscriptionId={}, actionType={}", subscription.getId(), actionType);

            return savedHistory;
        } catch (Exception e) {
            log.error("Failed to serialize subscription history for subscriptionId={}", subscription.getId(), e);
            throw new CustomException("Failed to serialize subscription history", e);
        }
    }

    // =====================================================
    // 内部処理: Subscriptionクローン（スナップショット）
    // =====================================================
    private Subscription cloneSubscription(Subscription original) {
        if (original == null) return null;

        return Subscription.builder()
                .id(original.getId())
                .user(original.getUser())
                .name(original.getName())
                .price(original.getPrice())
                .billingCycle(original.getBillingCycle())
                .category(original.getCategory())
                .nextPaymentDate(original.getNextPaymentDate())
                .createdAt(original.getCreatedAt())
                .isDeleted(original.isDeleted())
                .build();
    }
}