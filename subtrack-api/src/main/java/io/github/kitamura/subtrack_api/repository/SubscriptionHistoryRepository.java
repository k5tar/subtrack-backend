package io.github.kitamura.subtrack_api.repository;

import io.github.kitamura.subtrack_api.entity.Subscription;
import io.github.kitamura.subtrack_api.entity.SubscriptionHistory;
import io.github.kitamura.subtrack_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionHistoryRepository extends JpaRepository<SubscriptionHistory, Long> {

    List<SubscriptionHistory> findByUserOrderByChangedAtDesc(User user);

    List<SubscriptionHistory> findBySubscriptionOrderByChangedAtDesc(Subscription subscription);
}