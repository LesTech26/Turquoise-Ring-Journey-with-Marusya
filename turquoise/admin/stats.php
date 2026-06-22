<?php
/**
 * admin/stats.php
 * Карточки статистики и простые таблицы рейтингов.
 */
require_once __DIR__ . '/../includes/config.php';
require_once __DIR__ . '/../includes/db.php';
require_once __DIR__ . '/../includes/functions.php';
require_once __DIR__ . '/../includes/auth.php';
requireRole('admin', 'editor');

$adminTitle  = 'Статистика';
$adminActive = 'stats';

$stats = [
    'users'       => (int) db()->query('SELECT COUNT(*) FROM users')->fetchColumn(),
    'districts'   => (int) db()->query('SELECT COUNT(*) FROM districts WHERE is_active = 1')->fetchColumn(),
    'games'       => (int) db()->query('SELECT COUNT(*) FROM game_history')->fetchColumn(),
    'achievements'=> (int) db()->query('SELECT COUNT(*) FROM user_achievements')->fetchColumn(),
];

$topDistricts = db()->query(
    "SELECT d.name, COUNT(up.id) AS completions
     FROM districts d
     LEFT JOIN user_progress up ON up.district_id = d.id AND up.is_completed = 1
     GROUP BY d.id
     ORDER BY completions DESC
     LIMIT 10"
)->fetchAll();

ob_start();
?>
<div class="stat-grid">
    <div class="stat-card">
        <span class="stat-card__value"><?= $stats['users'] ?></span>
        <span class="stat-card__label">Всего пользователей</span>
    </div>
    <div class="stat-card">
        <span class="stat-card__value"><?= $stats['districts'] ?></span>
        <span class="stat-card__label">Активных районов</span>
    </div>
    <div class="stat-card">
        <span class="stat-card__value"><?= $stats['games'] ?></span>
        <span class="stat-card__label">Сыграно партий</span>
    </div>
    <div class="stat-card">
        <span class="stat-card__value"><?= $stats['achievements'] ?></span>
        <span class="stat-card__label">Выдано достижений</span>
    </div>
</div>

<div class="admin-table-wrap">
    <table class="admin-table">
        <thead><tr><th>Район</th><th>Прохождений</th></tr></thead>
        <tbody>
            <?php foreach ($topDistricts as $row): ?>
                <tr>
                    <td><?= e($row['name']) ?></td>
                    <td><?= (int)$row['completions'] ?></td>
                </tr>
            <?php endforeach; ?>
        </tbody>
    </table>
</div>
<?php
$adminBody = ob_get_clean();
require __DIR__ . '/_layout.php';
