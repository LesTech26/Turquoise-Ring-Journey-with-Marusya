<?php
/**
 * functions.php
 * Общие хелперы, используемые по всему приложению.
 */

// --- Экранирование вывода -------------------------------------------------
function e(?string $value): string
{
    return htmlspecialchars($value ?? '', ENT_QUOTES, 'UTF-8');
}

// --- Редирект --------------------------------------------------------------
function redirect(string $path): void
{
    $path = '/' . ltrim($path, '/');
    header('Location: ' . BASE_URL . $path);
    exit;
}

// --- CSRF --------------------------------------------------------------
function csrf_token(): string
{
    if (empty($_SESSION['csrf_token'])) {
        $_SESSION['csrf_token'] = bin2hex(random_bytes(32));
    }
    return $_SESSION['csrf_token'];
}

function csrf_field(): string
{
    return '<input type="hidden" name="csrf_token" value="' . e(csrf_token()) . '">';
}

function csrf_verify(): bool
{
    $token = $_POST['csrf_token'] ?? '';
    return is_string($token) && !empty($_SESSION['csrf_token']) && hash_equals($_SESSION['csrf_token'], $token);
}

// --- Flash-сообщения --------------------------------------------------------
function setFlash(string $type, string $message): void
{
    $_SESSION['flash'][] = ['type' => $type, 'message' => $message];
}

function getFlash(): array
{
    $flash = $_SESSION['flash'] ?? [];
    unset($_SESSION['flash']);
    return $flash;
}

// --- Авторизация (общие шорткаты, дублируют Auth для удобства) -----------
function isLoggedIn(): bool
{
    return !empty($_SESSION['user']['id']);
}

function currentUser(): ?array
{
    return $_SESSION['user'] ?? null;
}

function requireLogin(): void
{
    if (!isLoggedIn()) {
        redirect('/login?redirect=' . urlencode($_SERVER['REQUEST_URI'] ?? '/profile'));
    }
}

function requireRole(string ...$roles): void
{
    requireLogin();
    $user = currentUser();
    if (!in_array($user['role'] ?? 'user', $roles, true)) {
        http_response_code(403);
        die('Доступ запрещён.');
    }
}

// --- Загрузка файлов -----------------------------------------------------
function uploadFile(array $file, string $subdir): ?string
{
    if (empty($file['tmp_name']) || $file['error'] !== UPLOAD_ERR_OK) {
        return null;
    }
    if ($file['size'] > UPLOAD_MAX_SIZE) {
        return null;
    }

    $finfo = new finfo(FILEINFO_MIME_TYPE);
    $mime  = $finfo->file($file['tmp_name']);
    if (!in_array($mime, UPLOAD_ALLOWED_MIME, true)) {
        return null;
    }

    $ext = match ($mime) {
        'image/jpeg' => 'jpg',
        'image/png'  => 'png',
        'image/webp' => 'webp',
        default      => 'bin',
    };

    $dir = rtrim(UPLOAD_PATH, '/') . '/' . trim($subdir, '/');
    if (!is_dir($dir)) {
        mkdir($dir, 0775, true);
    }

    $filename = bin2hex(random_bytes(16)) . '.' . $ext;
    $destination = $dir . '/' . $filename;

    if (!move_uploaded_file($file['tmp_name'], $destination)) {
        return null;
    }

    return trim($subdir, '/') . '/' . $filename;
}

// --- Прочие утилиты ---------------------------------------------------------
function slugify(string $text): string
{
    $text = mb_strtolower($text, 'UTF-8');
    $text = preg_replace('/[^a-z0-9-]+/', '-', $text);
    return trim($text, '-');
}

function formatDate(?string $datetime, string $format = 'd.m.Y H:i'): string
{
    if (!$datetime) {
        return '—';
    }
    $ts = strtotime($datetime);
    return $ts ? date($format, $ts) : '—';
}
