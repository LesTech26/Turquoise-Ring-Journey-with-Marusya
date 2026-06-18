<?php
/**
 * API: Сохранение собранного костюма
 * POST /api/costume-save
 *
 * {
 *   "district_id": 5,
 *   "elements": [1, 3, 7, 12],
 *   "screenshot": "data:image/png;base64,..."
 * }
 */
define('BASE_PATH', dirname(__DIR__));
require_once dirname(__DIR__) . '/includes/config.php';
require_once dirname(__DIR__) . '/includes/db.php';
require_once dirname(__DIR__) . '/includes/functions.php';
require_once dirname(__DIR__) . '/includes/auth.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonResponse(['success' => false, 'error' => 'Method Not Allowed'], 405);
}

if (!isLoggedIn()) {
    jsonResponse(['success' => false, 'error' => 'Unauthorized'], 401);
}

$csrfHeader = $_SERVER['HTTP_X_CSRF_TOKEN'] ?? '';
if (!hash_equals($_SESSION[CSRF_TOKEN_NAME] ?? '', $csrfHeader)) {
    jsonResponse(['success' => false, 'error' => 'CSRF token mismatch'], 403);
}

$body = json_decode(file_get_contents('php://input'), true);
if (!$body) {
    jsonResponse(['success' => false, 'error' => 'Invalid JSON'], 400);
}

$userId     = (int)currentUser()['id'];
$districtId = (int)($body['district_id'] ?? 0);
$elements   = array_filter(array_map('intval', $body['elements'] ?? []));
$screenshot = $body['screenshot'] ?? null;

if (!$elements) {
    jsonResponse(['success' => false, 'error' => 'No elements provided'], 400);
}

// Сохранить скриншот (base64 → файл)
$screenshotPath = null;
if ($screenshot && str_starts_with($screenshot, 'data:image/png;base64,')) {
    $data = base64_decode(substr($screenshot, strlen('data:image/png;base64,')));
    if ($data) {
        $dir  = UPLOAD_DIR . 'costumes/';
        if (!is_dir($dir)) mkdir($dir, 0755, true);
        $name = uniqid('costume_', true) . '.png';
        file_put_contents($dir . $name, $data);
        $screenshotPath = 'costumes/' . $name;
    }
}

// Сохранить костюм
db()->prepare(
    'INSERT INTO saved_costumes (user_id, district_id, elements, screenshot) VALUES (?, ?, ?, ?)'
)->execute([$userId, $districtId, json_encode($elements), $screenshotPath]);

// Отметить флаг collected_costume в прогрессе
db()->prepare(
    'INSERT INTO user_progress (user_id, district_id, collected_costume)
     VALUES (?, ?, 1)
     ON DUPLICATE KEY UPDATE collected_costume = 1'
)->execute([$userId, $districtId]);

jsonResponse(['success' => true]);
