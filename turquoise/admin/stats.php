<?php
define('BASE_PATH', dirname(__DIR__));
require_once dirname(__DIR__) . '/includes/config.php';
require_once dirname(__DIR__) . '/includes/db.php';
require_once dirname(__DIR__) . '/includes/functions.php';
require_once dirname(__DIR__) . '/includes/auth.php';

requireAdmin();

// ------------------------------------------------------------------
// Экспорт в CSV
// ------------------------------------------------------------------
if (($_GET['export'] ?? '') === 'csv') {
    $report = $_GET['report'] ?? 'users';

    $queries = [
        'users'     => 'SELECT id, username, email, role, is_active, created_at FROM users ORDER BY created_at DESC',
        'progress'  => 'SELECT u.username, d.name AS district, up.quiz_score, up.is_completed, up.updated_at
                        FROM user_progress up
                        JOIN users u ON u.id = up.user_id
                        JOIN districts d ON d.id = up.district_id
                        ORDER BY up.updated_at DESC',
        'games'     => 'SELECT u.username, gh.game_type, d.name AS district, gh.score, gh.duration, gh.played_at
                        FROM game_history gh
                        JOIN users u ON u.id = gh.user_id
                        LEFT JOIN districts d ON d.id = gh.district_id
                        ORDER BY gh.played_at DESC',
    ];

    if (!isset($queries[$report])) {
        die('Неизвестный отчёт.');
    }

    $rows = db()->query($queries[$report])->fetchAll();

    header('Content-Type: text/csv; charset=UTF-8');
    header('Content-Disposition: attachment; filename="' . $report . '_' . date('Y-m-d') . '.csv"');
    header('Pragma: no-cache');

    // BOM для Excel
    echo "\xEF\xBB\xBF";

    $out = fopen('php://output', 'w');
    if ($rows) {
        fputcsv($out, array_keys($rows[0]), ';');
        foreach ($rows as $row) {
            fputcsv($out, $row, ';');
        }
    }
    fclose($out);
    exit;
}

// ------------------------------------------------------------------
// Агрегированная статистика
// ------------------------------------------------------------------
$totalUsers     = (int)db()->query('SELECT COUNT(*) FROM users')->fetchColumn();
$totalCompleted = (int)db()->query('SELECT COUNT(*) FROM user_progress WHERE is_completed = 1')->fetchColumn();
$totalGames     = (int)db()->query('SELECT COUNT(*) FROM game_history')->fetchColumn();

$popularDistricts = db()->query(
    'SELECT d.name, COUNT(up.id) AS cnt
     FROM districts d
     LEFT JOIN user_progress up ON d.id = up.district_id
     GROUP BY d.id ORDER BY cnt DESC LIMIT 10'
)->fetchAll();

$gameStats = db()->query(
    'SELECT game_type, COUNT(*) AS cnt, AVG(score) AS avg_score
     FROM game_history
     GROUP BY game_type ORDER BY cnt DESC'
)->fetchAll();

$dailyActivity = db()->query(
    'SELECT DATE(played_at) AS day, COUNT(*) AS cnt
     FROM game_history
     WHERE played_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
     GROUP BY DATE(played_at)
     ORDER BY day ASC'
)->fetchAll();

include dirname(__DIR__) . '/templates/admin/header.php';
?>

<h1 class="admin-title">Статистика</h1>

<div class="stats-grid">
    <div class="stat-card"><span class="stat-value"><?= $totalUsers ?></span><span class="stat-label">Пользователей</span></div>
    <div class="stat-card"><span class="stat-value"><?= $totalCompleted ?></span><span class="stat-label">Районов пройдено</span></div>
    <div class="stat-card"><span class="stat-value"><?= $totalGames ?></span><span class="stat-label">Игр сыграно</span></div>
</div>

<h2>Популярные районы</h2>
<table class="admin-table">
    <thead><tr><th>Район</th><th>Посещений</th></tr></thead>
    <tbody>
    <?php foreach ($popularDistricts as $d): ?>
        <tr><td><?= e($d['name']) ?></td><td><?= e($d['cnt']) ?></td></tr>
    <?php endforeach; ?>
    </tbody>
</table>

<h2>Статистика по играм</h2>
<table class="admin-table">
    <thead><tr><th>Игра</th><th>Сыграно</th><th>Средний счёт</th></tr></thead>
    <tbody>
    <?php foreach ($gameStats as $g): ?>
        <tr>
            <td><?= e($g['game_type']) ?></td>
            <td><?= e($g['cnt']) ?></td>
            <td><?= number_format((float)$g['avg_score'], 1) ?></td>
        </tr>
    <?php endforeach; ?>
    </tbody>
</table>

<h2>Экспорт отчётов</h2>
<ul>
    <li><a href="?export=csv&report=users">Экспорт пользователей (CSV)</a></li>
    <li><a href="?export=csv&report=progress">Экспорт прогресса (CSV)</a></li>
    <li><a href="?export=csv&report=games">Экспорт истории игр (CSV)</a></li>
</ul>

<?php include dirname(__DIR__) . '/templates/admin/footer.php'; ?>
