package io.github.kitamura.subtrack_api.mapper;

import io.github.kitamura.subtrack_api.dto.SubscriptionRequestDto;
import io.github.kitamura.subtrack_api.dto.SubscriptionResponseDto;
import io.github.kitamura.subtrack_api.entity.Subscription;
import io.github.kitamura.subtrack_api.entity.User;

public class SubscriptionMapper {

    public static Subscription toEntity(SubscriptionRequestDto dto, User user) {
        return Subscription.builder()
                .user(user)
                .name(dto.getName())
                .price(dto.getPrice())
                .billingCycle(dto.getBillingCycle())
                .nextPaymentDate(dto.getNextPaymentDate())
                .category(dto.getCategory())
                .build();
    }

    public static SubscriptionResponseDto toDto(Subscription entity) {
        return SubscriptionResponseDto.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .name(entity.getName())
                .price(entity.getPrice())
                .billingCycle(entity.getBillingCycle())
                .nextPaymentDate(entity.getNextPaymentDate())
                .category(entity.getCategory())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static void updateEntityFromDto(SubscriptionRequestDto dto, Subscription entity) {
        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        entity.setBillingCycle(dto.getBillingCycle());
        entity.setNextPaymentDate(dto.getNextPaymentDate());
        entity.setCategory(dto.getCategory());
    }
}