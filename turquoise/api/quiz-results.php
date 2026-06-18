<?php
/**
 * API: Сохранение результатов викторины
 * POST /api/quiz-results
 *
 * {
 *   "district_id": 5,
 *   "score": 80,
 *   "answers": {"1": "a", "2": "b"}
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
$score      = max(0, min(100, (int)($body['score'] ?? 0)));

// Записать в историю игр
db()->prepare(
    'INSERT INTO game_history (user_id, game_type, district_id, score) VALUES (?, ?, ?, ?)'
)->execute([$userId, 'quiz', $districtId, $score]);

// Обновить лучший результат в прогрессе
$stmt = db()->prepare(
    'INSERT INTO user_progress (user_id, district_id, quiz_score)
     VALUES (?, ?, ?)
     ON DUPLICATE KEY UPDATE quiz_score = GREATEST(quiz_score, VALUES(quiz_score))'
);
$stmt->execute([$userId, $districtId, $score]);

jsonResponse(['success' => true, 'score' => $score]);
