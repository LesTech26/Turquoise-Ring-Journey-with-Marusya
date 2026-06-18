<?php
/**
 * API: Сохранение прогресса пользователя
 * POST /api/save-progress
 *
 * Тело запроса (JSON):
 * {
 *   "district_id": 5,
 *   "is_completed": true,
 *   "quiz_score": 80,
 *   "collected_costume": false
 * }
 */
define('BASE_PATH', dirname(__DIR__));
require_once dirname(__DIR__) . '/includes/config.php';
require_once dirname(__DIR__) . '/includes/db.php';
require_once dirname(__DIR__) . '/includes/functions.php';
require_once dirname(__DIR__) . '/includes/auth.php';

header('Content-Type: application/json; charset=utf-8');

// Только POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonResponse(['success' => false, 'error' => 'Method Not Allowed'], 405);
}

// Авторизация
if (!isLoggedIn()) {
    jsonResponse(['success' => false, 'error' => 'Unauthorized'], 401);
}

// CSRF (для fetch-запросов — проверяем заголовок)
$csrfHeader = $_SERVER['HTTP_X_CSRF_TOKEN'] ?? '';
if (!hash_equals($_SESSION[CSRF_TOKEN_NAME] ?? '', $csrfHeader)) {
    jsonResponse(['success' => false, 'error' => 'CSRF token mismatch'], 403);
}

// Разбор тела запроса
$body = json_decode(file_get_contents('php://input'), true);
if (!$body) {
    jsonResponse(['success' => false, 'error' => 'Invalid JSON'], 400);
}

$userId      = (int)currentUser()['id'];
$districtId  = (int)($body['district_id'] ?? 0);
$isCompleted = (bool)($body['is_completed'] ?? false);
$quizScore   = max(0, (int)($body['quiz_score'] ?? 0));
$hasCostume  = (bool)($body['collected_costume'] ?? false);

// Проверка: район существует
$stmt = db()->prepare('SELECT id FROM districts WHERE id = ? AND is_active = 1');
$stmt->execute([$districtId]);
if (!$stmt->fetch()) {
    jsonResponse(['success' => false, 'error' => 'District not found'], 404);
}

// Upsert прогресса
$completedAt = $isCompleted ? date('Y-m-d H:i:s') : null;

$stmt = db()->prepare(
    'INSERT INTO user_progress (user_id, district_id, is_completed, quiz_score, collected_costume, completed_at)
     VALUES (?, ?, ?, ?, ?, ?)
     ON DUPLICATE KEY UPDATE
         is_completed      = GREATEST(is_completed, VALUES(is_completed)),
         quiz_score        = GREATEST(quiz_score, VALUES(quiz_score)),
         collected_costume = GREATEST(collected_costume, VALUES(collected_costume)),
         completed_at      = IF(is_completed = 0 AND VALUES(is_completed) = 1, VALUES(completed_at), completed_at)'
);
$stmt->execute([$userId, $districtId, $isCompleted, $quizScore, $hasCostume, $completedAt]);

// Проверить и выдать достижения
$newAchievements = checkAndGrantAchievements($userId);

jsonResponse([
    'success'          => true,
    'new_achievements' => $newAchievements,
]);

// ------------------------------------------------------------------
// Проверка условий достижений
// ------------------------------------------------------------------
function checkAndGrantAchievements(int $userId): array
{
    $granted = [];

    $completedCount = (int)db()->prepare(
        'SELECT COUNT(*) FROM user_progress WHERE user_id = ? AND is_completed = 1'
    )->execute([$userId]) ? db()->prepare(
        'SELECT COUNT(*) FROM user_progress WHERE user_id = ? AND is_completed = 1'
    )->execute([$userId]) : 0;

    // Пересчитаем правильно
    $stmt = db()->prepare('SELECT COUNT(*) FROM user_progress WHERE user_id = ? AND is_completed = 1');
    $stmt->execute([$userId]);
    $completedCount = (int)$stmt->fetchColumn();

    $milestones = [
        1  => 'first_district',
        5  => 'five_districts',
        12 => 'half_ring',
        24 => 'full_ring',
    ];

    foreach ($milestones as $count => $type) {
        if ($completedCount >= $count) {
            // Выдать, если ещё не выдано
            $exists = db()->prepare(
                'SELECT id FROM user_achievements WHERE user_id = ? AND achievement_type = ?'
            );
            $exists->execute([$userId, $type]);
            if (!$exists->fetch()) {
                db()->prepare(
                    'INSERT INTO user_achievements (user_id, achievement_type, achievement_value) VALUES (?, ?, ?)'
                )->execute([$userId, $type, (string)$count]);
                $granted[] = $type;
            }
        }
    }

    return $granted;
}
