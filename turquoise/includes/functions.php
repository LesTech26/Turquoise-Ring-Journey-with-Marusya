<?php
/**
 * Вспомогательные функции проекта
 */

/**
 * Экранирование вывода (XSS-защита)
 */
function e(string $str): string
{
    return htmlspecialchars($str, ENT_QUOTES, 'UTF-8');
}

/**
 * Редирект
 */
function redirect(string $url): void
{
    header('Location: ' . BASE_URL . $url);
    exit;
}

/**
 * Генерация CSRF-токена
 */
function csrf_token(): string
{
    if (empty($_SESSION[CSRF_TOKEN_NAME])) {
        $_SESSION[CSRF_TOKEN_NAME] = bin2hex(random_bytes(32));
    }
    return $_SESSION[CSRF_TOKEN_NAME];
}

/**
 * Проверка CSRF-токена
 */
function csrf_verify(): bool
{
    $token = $_POST[CSRF_TOKEN_NAME] ?? $_SERVER['HTTP_X_CSRF_TOKEN'] ?? '';
    return !empty($_SESSION[CSRF_TOKEN_NAME])
        && hash_equals($_SESSION[CSRF_TOKEN_NAME], $token);
}

/**
 * Поле с CSRF-токеном для форм
 */
function csrf_field(): string
{
    return '<input type="hidden" name="' . CSRF_TOKEN_NAME . '" value="' . csrf_token() . '">';
}

/**
 * Транслитерация для slug
 */
function slugify(string $text): string
{
    $translitMap = [
        'а'=>'a','б'=>'b','в'=>'v','г'=>'g','д'=>'d','е'=>'e','ё'=>'yo',
        'ж'=>'zh','з'=>'z','и'=>'i','й'=>'j','к'=>'k','л'=>'l','м'=>'m',
        'н'=>'n','о'=>'o','п'=>'p','р'=>'r','с'=>'s','т'=>'t','у'=>'u',
        'ф'=>'f','х'=>'kh','ц'=>'ts','ч'=>'ch','ш'=>'sh','щ'=>'sch',
        'ъ'=>'','ы'=>'y','ь'=>'','э'=>'e','ю'=>'yu','я'=>'ya',
    ];
    $text = mb_strtolower($text, 'UTF-8');
    $text = strtr($text, $translitMap);
    $text = preg_replace('/[^a-z0-9]+/', '-', $text);
    return trim($text, '-');
}

/**
 * Загрузка файла
 */
function uploadFile(array $file, string $subdir = '', array $allowedTypes = []): ?string
{
    if ($file['error'] !== UPLOAD_ERR_OK) {
        return null;
    }
    if ($file['size'] > MAX_FILE_SIZE) {
        return null;
    }
    $allowedTypes = $allowedTypes ?: ALLOWED_IMAGE_TYPES;
    $finfo = new finfo(FILEINFO_MIME_TYPE);
    $mime  = $finfo->file($file['tmp_name']);
    if (!in_array($mime, $allowedTypes, true)) {
        return null;
    }
    $ext      = pathinfo($file['name'], PATHINFO_EXTENSION);
    $filename = uniqid('', true) . '.' . strtolower($ext);
    $dir      = UPLOAD_DIR . ltrim($subdir, '/');
    if (!is_dir($dir)) {
        mkdir($dir, 0755, true);
    }
    $dest = $dir . '/' . $filename;
    if (!move_uploaded_file($file['tmp_name'], $dest)) {
        return null;
    }
    return ltrim($subdir, '/') . '/' . $filename;
}

/**
 * Пагинация
 */
function paginate(int $total, int $page, int $perPage = ITEMS_PER_PAGE): array
{
    $pages = (int)ceil($total / $perPage);
    $page  = max(1, min($page, $pages));
    return [
        'total'  => $total,
        'pages'  => $pages,
        'page'   => $page,
        'offset' => ($page - 1) * $perPage,
        'limit'  => $perPage,
    ];
}

/**
 * JSON-ответ для API
 */
function jsonResponse(array $data, int $code = 200): void
{
    http_response_code($code);
    header('Content-Type: application/json; charset=utf-8');
    echo json_encode($data, JSON_UNESCAPED_UNICODE);
    exit;
}

/**
 * Получение текущего пользователя из сессии
 */
function currentUser(): ?array
{
    return $_SESSION['user'] ?? null;
}

/**
 * Проверка: авторизован ли пользователь
 */
function isLoggedIn(): bool
{
    return !empty($_SESSION['user']['id']);
}

/**
 * Проверка роли пользователя
 */
function hasRole(string $role): bool
{
    return ($_SESSION['user']['role'] ?? '') === $role;
}

/**
 * Требовать авторизацию (редирект, если не залогинен)
 */
function requireLogin(): void
{
    if (!isLoggedIn()) {
        redirect('/login.php?redirect=' . urlencode($_SERVER['REQUEST_URI']));
    }
}

/**
 * Требовать роль администратора
 */
function requireAdmin(): void
{
    requireLogin();
    if (!hasRole('admin')) {
        http_response_code(403);
        die('Доступ запрещён.');
    }
}

/**
 * Flash-сообщения
 */
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
