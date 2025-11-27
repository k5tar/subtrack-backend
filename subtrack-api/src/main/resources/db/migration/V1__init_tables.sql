-- ==========================================
-- USERS TABLE
-- ==========================================
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- ==========================================
-- SUBSCRIPTIONS TABLE
-- ==========================================
CREATE TABLE subscriptions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    billing_cycle VARCHAR(20) NOT NULL,
    next_payment_date DATE NOT NULL,
    category VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_user_subscription_name UNIQUE(user_id, name)
);

-- ==========================================
-- SUBSCRIPTION HISTORY TABLE
-- ==========================================
CREATE TABLE subscription_history (
    id BIGSERIAL PRIMARY KEY,
    subscription_id BIGINT NOT NULL REFERENCES subscriptions(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    name VARCHAR(100) NOT NULL,
    price NUMERIC(10,2),
    billing_cycle VARCHAR(20),
    category VARCHAR(50),
    action_type VARCHAR(10) NOT NULL CHECK (action_type IN ('INSERT', 'UPDATE', 'DELETE')),
    previous_value JSONB,
    new_value JSONB,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- INDEXES
-- ==========================================
CREATE INDEX idx_subscriptions_user_id ON subscriptions(user_id);
CREATE INDEX idx_subscriptions_not_deleted ON subscriptions(user_id, is_deleted)
WHERE is_deleted = FALSE;

CREATE INDEX idx_history_user_id_changed_at ON subscription_history(user_id, changed_at DESC);
CREATE INDEX idx_history_subscription_id_changed_at ON subscription_history(subscription_id, changed_at DESC);

CREATE INDEX idx_users_not_deleted ON users(is_deleted)
WHERE is_deleted = FALSE;

-- ==========================================
-- SEQUENCE RESET (IDs start from 1)
-- ==========================================
ALTER SEQUENCE users_id_seq RESTART WITH 1;
ALTER SEQUENCE subscriptions_id_seq RESTART WITH 1;
ALTER SEQUENCE subscription_history_id_seq RESTART WITH 1;