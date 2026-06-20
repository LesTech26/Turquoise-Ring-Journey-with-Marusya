-- ============================================================
-- Миграции базы данных
-- «Путешествие по бирюзовому кольцу России»
-- MySQL 5.7+, кодировка: utf8mb4
-- ============================================================

CREATE DATABASE IF NOT EXISTS `turquoise_ring`
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `turquoise_ring`;

-- ------------------------------------------------------------
-- 1. Пользователи
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `users` (
    `id`                INT AUTO_INCREMENT PRIMARY KEY,
    `username`          VARCHAR(50)  NOT NULL,
    `email`             VARCHAR(150) NOT NULL UNIQUE,
    `password_hash`     VARCHAR(255) NOT NULL,
    `role`              ENUM('admin','editor','user') NOT NULL DEFAULT 'user',
    `avatar`            VARCHAR(255) DEFAULT NULL,
    `is_active`         BOOLEAN NOT NULL DEFAULT TRUE,
    `reset_token`       VARCHAR(64)  DEFAULT NULL,
    `reset_token_exp`   DATETIME     DEFAULT NULL,
    `remember_token`    VARCHAR(64)  DEFAULT NULL,
    `created_at`        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_email`        (`email`),
    INDEX `idx_role`         (`role`),
    INDEX `idx_reset_token`  (`reset_token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 2. Районы
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `districts` (
    `id`                   INT AUTO_INCREMENT PRIMARY KEY,
    `name`                 VARCHAR(100) NOT NULL,
    `slug`                 VARCHAR(100) NOT NULL UNIQUE,
    `short_description`    TEXT         DEFAULT NULL,
    `full_description`     TEXT         DEFAULT NULL,
    `children_description` TEXT         DEFAULT NULL,
    `costume_description`  TEXT         DEFAULT NULL,
    `coat_of_arms`         VARCHAR(255) DEFAULT NULL,
    `map_x`                INT          DEFAULT NULL,
    `map_y`                INT          DEFAULT NULL,
    `fun_facts`            JSON         DEFAULT NULL,
    `is_active`            BOOLEAN      NOT NULL DEFAULT TRUE,
    `sort_order`           INT          NOT NULL DEFAULT 0,
    `created_at`           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_slug`   (`slug`),
    INDEX `idx_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 3. Фотографии районов
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `district_photos` (
    `id`          INT AUTO_INCREMENT PRIMARY KEY,
    `district_id` INT          NOT NULL,
    `image_path`  VARCHAR(255) NOT NULL,
    `caption`     VARCHAR(200) DEFAULT NULL,
    `sort_order`  INT          NOT NULL DEFAULT 0,
    `created_at`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`district_id`) REFERENCES `districts`(`id`) ON DELETE CASCADE,
    INDEX `idx_district` (`district_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 4. Временная лента событий
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `timeline_events` (
    `id`          INT AUTO_INCREMENT PRIMARY KEY,
    `district_id` INT          NOT NULL,
    `year`        VARCHAR(20)  NOT NULL,
    `title`       VARCHAR(200) NOT NULL,
    `description` TEXT         DEFAULT NULL,
    `icon`        VARCHAR(50)  DEFAULT NULL,
    `sort_order`  INT          NOT NULL DEFAULT 0,
    `created_at`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`district_id`) REFERENCES `districts`(`id`) ON DELETE CASCADE,
    INDEX `idx_district` (`district_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 5. Элементы костюмов
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `costume_elements` (
    `id`          INT AUTO_INCREMENT PRIMARY KEY,
    `district_id` INT          NOT NULL,
    `name`        VARCHAR(100) NOT NULL,
    `description` TEXT         DEFAULT NULL,
    `image_path`  VARCHAR(255) DEFAULT NULL,
    `model_path`  VARCHAR(255) DEFAULT NULL,
    `category`    ENUM('head','torso','belt','legs','shoes','accessories') NOT NULL DEFAULT 'torso',
    `sort_order`  INT          NOT NULL DEFAULT 0,
    `created_at`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`district_id`) REFERENCES `districts`(`id`) ON DELETE CASCADE,
    INDEX `idx_district`  (`district_id`),
    INDEX `idx_category`  (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 6. Вопросы викторины
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `quiz_questions` (
    `id`             INT AUTO_INCREMENT PRIMARY KEY,
    `district_id`    INT          NOT NULL,
    `question`       TEXT         NOT NULL,
    `option_a`       VARCHAR(255) NOT NULL,
    `option_b`       VARCHAR(255) NOT NULL,
    `option_c`       VARCHAR(255) NOT NULL,
    `option_d`       VARCHAR(255) NOT NULL,
    `correct_answer` CHAR(1)      NOT NULL,
    `difficulty`     ENUM('easy','medium','hard') NOT NULL DEFAULT 'medium',
    `points`         INT          NOT NULL DEFAULT 10,
    `sort_order`     INT          NOT NULL DEFAULT 0,
    `created_at`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`district_id`) REFERENCES `districts`(`id`) ON DELETE CASCADE,
    INDEX `idx_district`   (`district_id`),
    INDEX `idx_difficulty` (`difficulty`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 7. Прогресс пользователей
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_progress` (
    `id`               INT AUTO_INCREMENT PRIMARY KEY,
    `user_id`          INT       NOT NULL,
    `district_id`      INT       NOT NULL,
    `is_completed`     BOOLEAN   NOT NULL DEFAULT FALSE,
    `quiz_score`       INT       NOT NULL DEFAULT 0,
    `collected_costume` BOOLEAN  NOT NULL DEFAULT FALSE,
    `completed_at`     DATETIME DEFAULT NULL,
    `updated_at`       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`)     REFERENCES `users`(`id`)     ON DELETE CASCADE,
    FOREIGN KEY (`district_id`) REFERENCES `districts`(`id`) ON DELETE CASCADE,
    UNIQUE KEY `uq_user_district` (`user_id`, `district_id`),
    INDEX `idx_user`     (`user_id`),
    INDEX `idx_district` (`district_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 8. Достижения пользователей
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_achievements` (
    `id`                INT AUTO_INCREMENT PRIMARY KEY,
    `user_id`           INT          NOT NULL,
    `achievement_type`  VARCHAR(50)  NOT NULL,
    `achievement_value` VARCHAR(100) DEFAULT NULL,
    `earned_at`         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    INDEX `idx_user` (`user_id`),
    INDEX `idx_type` (`achievement_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 9. История прохождения игр
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `game_history` (
    `id`          INT AUTO_INCREMENT PRIMARY KEY,
    `user_id`     INT          NOT NULL,
    `game_type`   VARCHAR(50)  NOT NULL,
    `district_id` INT          DEFAULT NULL,
    `score`       INT          NOT NULL DEFAULT 0,
    `duration`    INT          DEFAULT NULL COMMENT 'секунды',
    `played_at`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`)     REFERENCES `users`(`id`)     ON DELETE CASCADE,
    FOREIGN KEY (`district_id`) REFERENCES `districts`(`id`) ON DELETE SET NULL,
    INDEX `idx_user`      (`user_id`),
    INDEX `idx_game_type` (`game_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 10. Сохранённые костюмы
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `saved_costumes` (
    `id`          INT AUTO_INCREMENT PRIMARY KEY,
    `user_id`     INT       NOT NULL,
    `district_id` INT       NOT NULL,
    `elements`    JSON      NOT NULL COMMENT 'массив ID элементов костюма',
    `screenshot`  VARCHAR(255) DEFAULT NULL,
    `created_at`  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`)     REFERENCES `users`(`id`)     ON DELETE CASCADE,
    FOREIGN KEY (`district_id`) REFERENCES `districts`(`id`) ON DELETE CASCADE,
    INDEX `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- Начальные данные: администратор по умолчанию
-- Пароль: admin123  (bcrypt, cost=12)
-- ВАЖНО: сменить пароль после первого входа!
-- ------------------------------------------------------------
INSERT IGNORE INTO `users` (`username`, `email`, `password_hash`, `role`)
VALUES ('admin', 'admin@turquoise-ring.ru',
        '$2y$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'admin');

-- ------------------------------------------------------------
-- 24 района Орловской области
-- ------------------------------------------------------------
INSERT IGNORE INTO `districts` (`name`, `slug`, `sort_order`) VALUES
('Болховский район',          'bolkhovsky',          1),
('Верховский район',          'verkhovsky',          2),
('Глазуновский район',        'glazunovsky',         3),
('Дмитровский район',         'dmitrovsky',          4),
('Должанский район',          'dolzhansky',          5),
('Залегощенский район',       'zalegoshchensky',     6),
('Знаменский район',          'znamensky',           7),
('Колпнянский район',         'kolpnyansky',         8),
('Корсаковский район',        'korsakovsky',         9),
('Краснозоренский район',     'krasnozorensky',     10),
('Кромской район',            'kromsky',            11),
('Ливенский район',           'livensky',           12),
('Малоархангельский район',   'maloarkhangelsky',   13),
('Мценский район',            'mtsvensky',          14),
('Новодеревеньковский район', 'novoderevenkovsky',  15),
('Новосильский район',        'novosilsky',         16),
('Орловский район',           'orlovsky',           17),
('Покровский район',          'pokrovsky',          18),
('Свердловский район',        'sverdlovsky',        19),
('Сосковский район',          'soskovsky',          20),
('Троснянский район',         'trosnansky',         21),
('Урицкий район',             'uritsky',            22),
('Хотынецкий район',          'khotynetsky',        23),
('Шаблыкинский район',        'shablykinsky',       24);
