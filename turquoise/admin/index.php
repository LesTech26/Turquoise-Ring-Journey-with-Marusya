<?php
/**
 * admin/index.php
 * Дашборд: ключевые цифры и быстрые ссылки.
 */
require_once __DIR__ . '/../includes/config.php';
require_once __DIR__ . '/../includes/db.php';
require_once __DIR__ . '/../includes/functions.php';
require_once __DIR__ . '/../includes/auth.php';
requireRole('admin', 'editor');

$adminTitle  = 'Дашборд';
$adminActive = 'dashboard';

$stats = [
    'users'     => (int) db()->query('SELECT COUNT(*) FROM users')->fetchColumn(),
    'districts' => (int) db()->query('SELECT COUNT(*) FROM districts WHERE is_active = 1')->fetchColumn(),
    'games'     => (int) db()->query('SELECT COUNT(*) FROM game_history')->fetchColumn(),
    'completed' => (int) db()->query('SELECT COUNT(*) FROM user_progress WHERE is_completed = 1')->fetchColumn(),
];

ob_start();
?>
<div class="stat-grid">
    <div class="stat-card">
        <span class="stat-card__value"><?= $stats['users'] ?></span>
        <span class="stat-card__label">Пользователей</span>
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
        <span class="stat-card__value"><?= $stats['completed'] ?></span>
        <span class="stat-card__label">Районов пройдено</span>
    </div>
</div>

<div class="admin-table-wrap">
    <table class="admin-table">
        <thead><tr><th>Раздел</th><th>Описание</th><th></th></tr></thead>
        <tbody>
            <tr>
                <td>Районы</td>
                <td>Добавление и редактирование районов, гербов, сортировки</td>
                <td><a href="<?= BASE_URL ?>/admin/districts" class="btn btn--sm">Перейти</a></td>
            </tr>
            <tr>
                <td>Контент</td>
                <td>Описания, легенды, костюмы — WYSIWYG-редактирование</td>
                <td><a href="<?= BASE_URL ?>/admin/content" class="btn btn--sm">Перейти</a></td>
            </tr>
            <tr>
                <td>Пользователи</td>
                <td>Список пользователей, роли, блокировка</td>
                <td><a href="<?= BASE_URL ?>/admin/users" class="btn btn--sm">Перейти</a></td>
            </tr>
            <tr>
                <td>Статистика</td>
                <td>Общие показатели вовлечённости</td>
                <td><a href="<?= BASE_URL ?>/admin/stats" class="btn btn--sm">Перейти</a></td>
            </tr>
        </tbody>
    </table>
</div>
<?php
$adminBody = ob_get_clean();
require __DIR__ . '/_layout.php';
