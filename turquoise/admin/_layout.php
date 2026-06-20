<?php
/**
 * admin/_layout.php
 * Общая обёртка админ-панели: сайдбар + основная область.
 * Подключается ИЗ страниц admin/*.php, а не наоборот.
 *
 * Переменные:
 *   $adminTitle  string  заголовок страницы
 *   $adminBody   string  HTML содержимого (буферизуется через ob_start)
 *   $adminActive string  ключ активного раздела: dashboard|districts|content|users|stats
 */
require_once __DIR__ . '/../includes/config.php';
require_once __DIR__ . '/../includes/db.php';
require_once __DIR__ . '/../includes/functions.php';
require_once __DIR__ . '/../includes/auth.php';

requireRole('admin', 'editor');

$__adminUser = currentUser();

$__adminNav = [
    'dashboard' => ['label' => 'Дашборд',  'href' => '/admin',          'icon' => '🏠'],
    'districts' => ['label' => 'Районы',   'href' => '/admin/districts','icon' => '🗺️'],
    'content'   => ['label' => 'Контент',  'href' => '/admin/content',  'icon' => '📝'],
    'users'     => ['label' => 'Пользователи', 'href' => '/admin/users','icon' => '👥'],
    'stats'     => ['label' => 'Статистика', 'href' => '/admin/stats',  'icon' => '📊'],
];

$pageTitle = $adminTitle ?? 'Админ-панель';
$bodyClass = 'admin-body';
?>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><?= e($pageTitle) ?> — Админ-панель</title>
    <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@600;700&family=Jost:wght@400;500;600;700&family=Comfortaa:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<?= BASE_URL ?>/assets/css/style.css">
</head>
<body>

<div class="admin-layout">
    <aside class="admin-sidebar" id="adminSidebar">
        <div class="admin-sidebar__brand">Бирюзовое кольцо</div>
        <?php foreach ($__adminNav as $key => $item): ?>
            <a href="<?= BASE_URL . $item['href'] ?>" class="<?= $key === ($adminActive ?? '') ? 'is-active' : '' ?>">
                <?= $item['icon'] ?> <?= e($item['label']) ?>
            </a>
        <?php endforeach; ?>
        <a href="<?= BASE_URL ?>/" style="margin-top:auto;">← Вернуться на сайт</a>
        <a href="<?= BASE_URL ?>/logout">Выйти</a>
    </aside>

    <main class="admin-main">
        <button type="button" class="btn btn--ghost admin-mobile-toggle" id="adminSidebarToggle">☰ Меню</button>

        <div class="admin-main__header">
            <h1><?= e($pageTitle) ?></h1>
            <span style="font-family:var(--font-heading); color:var(--color-walnut);">
                <?= e($__adminUser['username'] ?? '') ?> · <?= e($__adminUser['role'] ?? '') ?>
            </span>
        </div>

        <?php foreach (getFlash() as $f): ?>
            <div class="alert alert--<?= e($f['type']) ?>"><?= e($f['message']) ?></div>
        <?php endforeach; ?>

        <?= $adminBody ?? '' ?>
    </main>
</div>

<script src="<?= BASE_URL ?>/assets/js/main.js" defer></script>
</body>
</html>
