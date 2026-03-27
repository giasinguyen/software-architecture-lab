-- ============================================================
-- Chạy script này 1 lần trước khi start app:
--   mysql -u root -p < sql/init.sql
-- ============================================================

-- ── HORIZONTAL DB ──────────────────────────────────────────
CREATE DATABASE IF NOT EXISTS horizontal_db;
USE horizontal_db;

CREATE TABLE IF NOT EXISTS user_male (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(100),
    gender     CHAR(1) DEFAULT 'M',
    created_at DATETIME DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS user_female (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(100),
    gender     CHAR(1) DEFAULT 'F',
    created_at DATETIME DEFAULT NOW()
);

-- seed data để benchmark
INSERT INTO user_male (name, email)
SELECT CONCAT('Nam_', seq), CONCAT('male', seq, '@demo.com')
FROM (
    WITH RECURSIVE r(seq) AS (
        SELECT 1 UNION ALL SELECT seq+1 FROM r WHERE seq < 10000
    ) SELECT seq FROM r
) t;

INSERT INTO user_female (name, email)
SELECT CONCAT('Nu_', seq), CONCAT('female', seq, '@demo.com')
FROM (
    WITH RECURSIVE r(seq) AS (
        SELECT 1 UNION ALL SELECT seq+1 FROM r WHERE seq < 10000
    ) SELECT seq FROM r
) t;

-- ── VERTICAL DB ────────────────────────────────────────────
CREATE DATABASE IF NOT EXISTS vertical_db;
USE vertical_db;

-- Bảng GỐC chưa tách (để so sánh performance)
CREATE TABLE IF NOT EXISTS user_full (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    name       VARCHAR(100),
    email      VARCHAR(100),
    phone      VARCHAR(20),
    avatar_url VARCHAR(255),
    bio        TEXT,
    last_login DATETIME,
    settings   JSON,
    created_at DATETIME DEFAULT NOW()
);

-- Bảng ĐÃ TÁCH (vertical partition)
CREATE TABLE IF NOT EXISTS user_basic (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    name       VARCHAR(100),
    email      VARCHAR(100),
    phone      VARCHAR(20),
    created_at DATETIME DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS user_profile (
    id         BIGINT PRIMARY KEY,
    avatar_url VARCHAR(255),
    bio        TEXT,
    FOREIGN KEY (id) REFERENCES user_basic(id)
);

CREATE TABLE IF NOT EXISTS user_activity (
    id         BIGINT PRIMARY KEY,
    last_login DATETIME,
    settings   JSON,
    FOREIGN KEY (id) REFERENCES user_basic(id)
);

-- seed 10000 rows vào cả 2 dạng để benchmark
INSERT INTO user_full (name, email, phone, avatar_url, bio, last_login, settings)
SELECT
    CONCAT('User_', seq),
    CONCAT('user', seq, '@demo.com'),
    CONCAT('09', LPAD(seq, 8, '0')),
    CONCAT('https://cdn.demo.com/avatar/', seq, '.jpg'),
    REPEAT('Lorem ipsum bio text for user. ', 10),
    NOW(),
    '{"theme":"dark","lang":"vi","notify":true}'
FROM (
    WITH RECURSIVE r(seq) AS (
        SELECT 1 UNION ALL SELECT seq+1 FROM r WHERE seq < 10000
    ) SELECT seq FROM r
) t;

INSERT INTO user_basic (name, email, phone)
SELECT name, email, phone FROM user_full;

INSERT INTO user_profile (id, avatar_url, bio)
SELECT id, avatar_url, bio FROM user_full;

INSERT INTO user_activity (id, last_login, settings)
SELECT id, last_login, settings FROM user_full;

-- ── FUNCTIONAL DB ──────────────────────────────────────────
CREATE DATABASE IF NOT EXISTS user_db;
USE user_db;

CREATE TABLE IF NOT EXISTS users (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    username   VARCHAR(100) NOT NULL,
    email      VARCHAR(100),
    role       VARCHAR(50) DEFAULT 'USER',
    created_at DATETIME DEFAULT NOW()
);

INSERT INTO users (username, email, role)
SELECT CONCAT('user_', seq), CONCAT('u', seq, '@demo.com'),
       IF(seq % 10 = 0, 'ADMIN', 'USER')
FROM (
    WITH RECURSIVE r(seq) AS (
        SELECT 1 UNION ALL SELECT seq+1 FROM r WHERE seq < 1000
    ) SELECT seq FROM r
) t;

CREATE DATABASE IF NOT EXISTS order_db;
USE order_db;

CREATE TABLE IF NOT EXISTS orders (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id    BIGINT NOT NULL,
    total      DECIMAL(10,2),
    status     VARCHAR(50) DEFAULT 'PENDING',
    created_at DATETIME DEFAULT NOW()
);

INSERT INTO orders (user_id, total, status)
SELECT
    FLOOR(1 + RAND() * 1000),
    ROUND(10 + RAND() * 990, 2),
    ELT(1 + FLOOR(RAND() * 3), 'PENDING', 'PAID', 'SHIPPED')
FROM (
    WITH RECURSIVE r(seq) AS (
        SELECT 1 UNION ALL SELECT seq+1 FROM r WHERE seq < 5000
    ) SELECT seq FROM r
) t;
