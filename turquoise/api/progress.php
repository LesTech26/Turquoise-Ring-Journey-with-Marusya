<?php
/**
 * API: Получение прогресса пользователя
 * GET /api/progress
 *
 * Используется для синхронизации между устройствами.
 * Если пользователь не авторизован — возвращает 401.
 */
define('BASE_PATH', dirname(__DIR__));
require_once dirname(__DIR__) . '/includes/config.php';
require_once dirname(__DIR__) . '/includes/db.php';
require_once dirname(__DIR__) . '/includes/functions.php';
require_once dirname(__DIR__) . '/includes/auth.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    jsonResponse(['success' => false, 'error' => 'Method Not Allowed'], 405);
}

if (!isLoggedIn()) {
    jsonResponse(['success' => false, 'error' => 'Unauthorized'], 401);
}

$userId = (int)currentUser()['id'];

// Прогресс по районам
$stmt = db()->prepare(
    'SELECT district_id, is_completed, quiz_score, collected_costume, completed_at, updated_at
     FROM user_progress
     WHERE user_id = ?'
);
$stmt->execute([$userId]);
$progress = $stmt->fetchAll();

// Достижения
$stmt = db()->prepare(
    'SELECT achievement_type, achievement_value, earned_at
     FROM user_achievements
     WHERE user_id = ?'
);
$stmt->execute([$userId]);
$achievements = $stmt->fetchAll();

// Итоговый счёт
$totalScore = array_sum(array_column($progress, 'quiz_score'));

jsonResponse([
    'success'      => true,
    'user_id'      => $userId,
    'progress'     => $progress,
    'achievements' => $achievements,
    'total_score'  => $totalScore,
    'synced_at'    => date('c'),
]);
