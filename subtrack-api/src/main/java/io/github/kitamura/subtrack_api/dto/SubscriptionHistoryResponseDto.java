package io.github.kitamura.subtrack_api.dto;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.kitamura.subtrack_api.entity.Subscription;
import io.github.kitamura.subtrack_api.entity.SubscriptionHistory;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class SubscriptionHistoryResponseDto {

    private Long subscriptionId;
    private String name;
    private BigDecimal price;
    private String billingCycle;
    private String category;
    private String actionType;
    private LocalDateTime changedAt;

    private JsonNode previousValue;
    private JsonNode newValue;

    // Subscription + History から作成（作成・更新・削除時）
    public static SubscriptionHistoryResponseDto of(Subscription subscription, SubscriptionHistory history) {
        return SubscriptionHistoryResponseDto.builder()
                .subscriptionId(subscription.getId())
                .name(subscription.getName())
                .price(subscription.getPrice())
                .billingCycle(subscription.getBillingCycle())
                .category(subscription.getCategory())
                .actionType(history.getActionType())
                .changedAt(history.getChangedAt())
                .previousValue(history.getPreviousValue())
                .newValue(history.getNewValue())
                .build();
    }

    // Historyのみから作成（履歴取得時）
    public static SubscriptionHistoryResponseDto fromHistory(SubscriptionHistory history) {
        return SubscriptionHistoryResponseDto.builder()
                .subscriptionId(history.getSubscription().getId())
                .name(history.getName())
                .price(history.getPrice())
                .billingCycle(history.getBillingCycle())
                .category(history.getCategory())
                .actionType(history.getActionType())
                .changedAt(history.getChangedAt())
                .previousValue(history.getPreviousValue())
                .newValue(history.getNewValue())
                .build();
    }
}