<?php
/**
 * config.php
 * Базовые константы окружения.
 * Эти настройки нужно подправить под реальный сервер при деплое.
 */

if (!defined('BASE_PATH')) {
    define('BASE_PATH', dirname(__DIR__));
}

// --- Окружение -------------------------------------------------------
define('APP_ENV', 'development'); // 'development' | 'production'
define('APP_DEBUG', APP_ENV === 'development');

// --- URL ---------------------------------------------------------------
// Базовый префикс пути приложения (см. RewriteBase в .htaccess)
define('BASE_URL', '/turquoise');
define('UPLOAD_URL', BASE_URL . '/uploads/');
define('UPLOAD_PATH', BASE_PATH . '/uploads/');

// --- База данных ---------------------------------------------------------
define('DB_HOST', 'localhost');
define('DB_NAME', 'turquoise_ring');
define('DB_USER', 'root');
define('DB_PASS', '');
define('DB_CHARSET', 'utf8mb4');

// --- Безопасность --------------------------------------------------------
define('HASH_COST', 12);
define('REMEMBER_TOKEN_DAYS', 30);
define('RESET_TOKEN_TTL_MIN', 60);

// --- Загрузка файлов -----------------------------------------------------
define('UPLOAD_MAX_SIZE', 10 * 1024 * 1024); // 10 МБ
define('UPLOAD_ALLOWED_MIME', ['image/jpeg', 'image/png', 'image/webp']);

// --- Вывод ошибок ----------------------------------------------------------
if (APP_DEBUG) {
    error_reporting(E_ALL);
    ini_set('display_errors', '1');
} else {
    error_reporting(0);
    ini_set('display_errors', '0');
}

// --- Сессия ------------------------------------------------------------
if (session_status() === PHP_SESSION_NONE) {
    session_set_cookie_params([
        'lifetime' => 0,
        'path'     => BASE_URL . '/',
        'httponly' => true,
        'samesite' => 'Lax',
    ]);
    session_start();
}

date_default_timezone_set('Europe/Moscow');
