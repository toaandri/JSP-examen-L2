-- PostgreSQL schema for Tinder-like JSP app
-- Structure only (no data). Data lives in db/seed.sql

CREATE TABLE IF NOT EXISTS app_user (
    id              BIGSERIAL PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    last_login_at   TIMESTAMPTZ NULL
);

CREATE TABLE IF NOT EXISTS profile (
    user_id             BIGINT PRIMARY KEY REFERENCES app_user(id) ON DELETE CASCADE,
    first_name          VARCHAR(80)  NOT NULL,
    last_name           VARCHAR(80)  NOT NULL,
    birthdate           DATE         NOT NULL,
    sex_at_birth        VARCHAR(20)  NULL,
    gender_identity     VARCHAR(40)  NOT NULL,
    sexual_orientation  VARCHAR(40)  NOT NULL,
    looking_for         VARCHAR(40)  NOT NULL,
    bio                 VARCHAR(500) NULL,
    city                VARCHAR(120) NULL,
    photo_url           VARCHAR(500) NULL,
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS interest (
    id      BIGSERIAL PRIMARY KEY,
    label   VARCHAR(80) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS user_interest (
    user_id     BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    interest_id BIGINT NOT NULL REFERENCES interest(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, interest_id)
);

CREATE TABLE IF NOT EXISTS onboarding_question (
    id             BIGSERIAL PRIMARY KEY,
    question_key   VARCHAR(80) NOT NULL UNIQUE,
    label          VARCHAR(200) NOT NULL,
    question_order INT NOT NULL,
    is_active      BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS onboarding_option (
    id             BIGSERIAL PRIMARY KEY,
    question_id    BIGINT NOT NULL REFERENCES onboarding_question(id) ON DELETE CASCADE,
    label          VARCHAR(120) NOT NULL,
    score_tag      VARCHAR(80) NOT NULL,
    option_order   INT NOT NULL,
    UNIQUE (question_id, label)
);

CREATE TABLE IF NOT EXISTS user_onboarding_answer (
    user_id        BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    question_id    BIGINT NOT NULL REFERENCES onboarding_question(id) ON DELETE CASCADE,
    option_id      BIGINT NOT NULL REFERENCES onboarding_option(id) ON DELETE CASCADE,
    answered_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (user_id, question_id)
);

CREATE TABLE IF NOT EXISTS swipe (
    id            BIGSERIAL PRIMARY KEY,
    from_user_id  BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    to_user_id    BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    decision      VARCHAR(10) NOT NULL CHECK (decision IN ('LIKE', 'NOPE')),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (from_user_id, to_user_id),
    CHECK (from_user_id <> to_user_id)
);

CREATE TABLE IF NOT EXISTS app_match (
    id          BIGSERIAL PRIMARY KEY,
    user_a_id   BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    user_b_id   BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    status      VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE')),
    UNIQUE (user_a_id, user_b_id),
    CHECK (user_a_id < user_b_id)
);

CREATE TABLE IF NOT EXISTS notification (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    type        VARCHAR(40) NOT NULL CHECK (type IN ('NEW_MATCH')),
    payload_json TEXT NULL,
    read_at     TIMESTAMPTZ NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS message (
    id          BIGSERIAL PRIMARY KEY,
    match_id    BIGINT NOT NULL REFERENCES app_match(id) ON DELETE CASCADE,
    from_user_id BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    body        VARCHAR(1000) NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Optional safety: block users so they don't appear again
CREATE TABLE IF NOT EXISTS block (
    id            BIGSERIAL PRIMARY KEY,
    blocker_user_id BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    blocked_user_id BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (blocker_user_id, blocked_user_id),
    CHECK (blocker_user_id <> blocked_user_id)
);

-- Helpful indexes
CREATE INDEX IF NOT EXISTS idx_profile_city ON profile(city);
CREATE INDEX IF NOT EXISTS idx_user_interest_interest ON user_interest(interest_id);
CREATE INDEX IF NOT EXISTS idx_onboarding_question_order ON onboarding_question(question_order);
CREATE INDEX IF NOT EXISTS idx_onboarding_option_question ON onboarding_option(question_id, option_order);
CREATE INDEX IF NOT EXISTS idx_user_answer_user ON user_onboarding_answer(user_id, answered_at DESC);
CREATE INDEX IF NOT EXISTS idx_swipe_from ON swipe(from_user_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_swipe_to ON swipe(to_user_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_match_user_a ON app_match(user_a_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_match_user_b ON app_match(user_b_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_notification_user ON notification(user_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_message_match ON message(match_id, created_at ASC);
CREATE INDEX IF NOT EXISTS idx_block_blocker ON block(blocker_user_id, created_at DESC);

