package io.github.kitamura.subtrack_api.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing a SaaS subscription for a user.
 * <p>
 * Includes details such as name, price, billing cycle, next payment date, and category.
 * <p>
 * Mapped to the 'subscriptions' table in the database.
 * <p>
 * See HELP.md for validation rules and error handling related to subscriptions.
 */
@Entity
@Table(
    name = "subscriptions",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "name"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "billing_cycle", nullable = false, length = 20)
    private String billingCycle;

    @Column(name = "next_payment_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextPaymentDate;

    @Column(length = 50)
    private String category;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
}