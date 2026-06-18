<?php
define('BASE_PATH', dirname(__DIR__));
require_once dirname(__DIR__) . '/includes/config.php';
require_once dirname(__DIR__) . '/includes/db.php';
require_once dirname(__DIR__) . '/includes/functions.php';
require_once dirname(__DIR__) . '/includes/auth.php';

requireAdmin();

// Статистика для дашборда
$stats = [];

$stats['users']     = db()->query('SELECT COUNT(*) FROM users')->fetchColumn();
$stats['districts'] = db()->query('SELECT COUNT(*) FROM districts')->fetchColumn();
$stats['completed'] = db()->query('SELECT COUNT(*) FROM user_progress WHERE is_completed = 1')->fetchColumn();
$stats['games']     = db()->query('SELECT COUNT(*) FROM game_history')->fetchColumn();

// Популярные районы (топ-5)
$popularDistricts = db()->query(
    'SELECT d.name, COUNT(up.id) AS visits
     FROM districts d
     LEFT JOIN user_progress up ON d.id = up.district_id
     GROUP BY d.id
     ORDER BY visits DESC
     LIMIT 5'
)->fetchAll();

// Активность пользователей за последние 7 дней
$recentActivity = db()->query(
    'SELECT DATE(played_at) AS day, COUNT(*) AS count
     FROM game_history
     WHERE played_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
     GROUP BY DATE(played_at)
     ORDER BY day ASC'
)->fetchAll();

include dirname(__DIR__) . '/templates/admin/header.php';
?>

<h1 class="admin-title">Дашборд</h1>

<div class="stats-grid">
    <div class="stat-card">
        <span class="stat-value"><?= e($stats['users']) ?></span>
        <span class="stat-label">Пользователей</span>
    </div>
    <div class="stat-card">
        <span class="stat-value"><?= e($stats['districts']) ?></span>
        <span class="stat-label">Районов</span>
    </div>
    <div class="stat-card">
        <span class="stat-value"><?= e($stats['completed']) ?></span>
        <span class="stat-label">Пройдено районов</span>
    </div>
    <div class="stat-card">
        <span class="stat-value"><?= e($stats['games']) ?></span>
        <span class="stat-label">Игр сыграно</span>
    </div>
</div>

<h2>Популярные районы</h2>
<table class="admin-table">
    <thead><tr><th>Район</th><th>Посещений</th></tr></thead>
    <tbody>
    <?php foreach ($popularDistricts as $d): ?>
        <tr>
            <td><?= e($d['name']) ?></td>
            <td><?= e($d['visits']) ?></td>
        </tr>
    <?php endforeach; ?>
    </tbody>
</table>

<?php include dirname(__DIR__) . '/templates/admin/footer.php'; ?>
