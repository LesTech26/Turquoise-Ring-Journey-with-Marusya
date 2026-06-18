<?php
/**
 * Конфигурация проекта
 * «Путешествие по бирюзовому кольцу России»
 */
if (!defined('BASE_PATH')) {
    define('BASE_PATH', dirname(__FILE__, 2));
}
// Режим отладки
define('DEBUG', true);

if (DEBUG) {
    error_reporting(E_ALL);
    ini_set('display_errors', 1);
} else {
    error_reporting(0);
    ini_set('display_errors', 0);
}

// Базовый URL
define('BASE_URL', 'http://localhost/turquoise');

// База данных
define('DB_HOST', 'localhost');
define('DB_NAME', 'turquoise_ring');
define('DB_USER', 'root');
define('DB_PASS', '');
define('DB_CHARSET', 'utf8mb4');

// Сессия
define('SESSION_NAME', 'turquoise_session');
define('SESSION_LIFETIME', 86400 * 30); // 30 дней

// Загрузка файлов
define('UPLOAD_DIR', BASE_PATH . '/assets/uploads/');
define('UPLOAD_URL', BASE_URL . '/assets/uploads/');
define('MAX_FILE_SIZE', 10 * 1024 * 1024); // 10 MB
define('ALLOWED_IMAGE_TYPES', ['image/jpeg', 'image/png', 'image/webp', 'image/svg+xml']);
define('ALLOWED_MODEL_TYPES', ['model/gltf-binary', 'application/octet-stream']);

// Безопасность
define('CSRF_TOKEN_NAME', 'csrf_token');
define('HASH_COST', 12); // bcrypt cost

// Email (восстановление пароля)
define('MAIL_FROM', 'noreply@turquoise-ring.ru');
define('MAIL_FROM_NAME', 'Бирюзовое кольцо');
define('MAIL_HOST', 'smtp.example.com');
define('MAIL_PORT', 587);
define('MAIL_USER', '');
define('MAIL_PASS', '');

// Пагинация
define('ITEMS_PER_PAGE', 20);

// Запуск сессии
session_name(SESSION_NAME);
session_set_cookie_params([
    'lifetime' => SESSION_LIFETIME,
    'path'     => '/',
    'secure'   => false,
    'httponly' => true,
    'samesite' => 'Lax',
]);
session_start();

// Временная зона
date_default_timezone_set('Europe/Moscow');
