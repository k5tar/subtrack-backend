package io.github.kitamura.subtrack_api.repository;

import io.github.kitamura.subtrack_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 削除されていないユーザのみ取得
    @Query("SELECT u FROM User u WHERE u.isDeleted = false")
    List<User> findAllActive();

    // IDで削除されていないユーザのみ検索
    @Query("SELECT u FROM User u WHERE u.id = :id AND u.isDeleted = false")
    Optional<User> findActiveById(Long id);

    // Emailで削除されていないユーザを検索（ログイン用など）
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.isDeleted = false")
    Optional<User> findActiveByEmail(String email);
}