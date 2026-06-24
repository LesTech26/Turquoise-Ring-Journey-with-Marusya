<?php
/**
 * templates/header.php
 * Общая шапка сайта: логотип, навигация, профиль/вход, мобильное меню.
 */
if (!defined('BASE_PATH')) {
    require_once __DIR__ . '/../includes/config.php';
    require_once __DIR__ . '/../includes/db.php';
    require_once __DIR__ . '/../includes/functions.php';
    require_once __DIR__ . '/../includes/auth.php';
}

$__user = currentUser();
$__currentPath = trim((string) parse_url($_SERVER['REQUEST_URI'] ?? '/', PHP_URL_PATH), '/');

$__navItems = [
    ''             => 'Карта',
    'games'        => 'Игры',
    'achievements' => 'Достижения',
    'media'        => 'Медиатека',
    'about'        => 'О проекте',
];

function nav_is_active(string $key, string $current): string
{
    if ($key === '') {
        return $current === '' ? ' is-active' : '';
    }
    return ($current === $key || str_starts_with($current, $key . '/')) ? ' is-active' : '';
}
?>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><?= isset($pageTitle) ? e($pageTitle) . ' — ' : '' ?>Бирюзовое кольцо России</title>
    <meta name="description" content="Интерактивное путешествие по районам Орловской области: легенды, костюмы, викторины и игры.">

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:ital,wght@0,500;0,600;0,700;1,500&family=Jost:wght@400;500;600;700&family=Comfortaa:wght@400;500;700&family=Lobster&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="<?= BASE_URL ?>/assets/css/style.css?v=<?= ASSET_VERSION ?>">
    <?php if (!empty($extraStyles)): foreach ($extraStyles as $href): ?>
        <link rel="stylesheet" href="<?= e($href) ?>">
    <?php endforeach; endif; ?>
</head>
<body class="<?= e($bodyClass ?? '') ?>">

<a class="skip-link" href="#main">Перейти к содержимому</a>

<header class="site-header">
    <div class="site-header__inner container">

        <a href="<?= BASE_URL ?>/" class="logo">
            <span class="logo__mark" aria-hidden="true">
                <svg viewBox="0 0 48 48" width="40" height="40" focusable="false">
                    <circle cx="24" cy="24" r="21" fill="var(--color-turquoise)"/>
                    <path d="M24 8c5 6 10 9 10 16a10 10 0 1 1-20 0c0-7 5-10 10-16Z" fill="var(--color-cream)"/>
                    <circle cx="24" cy="26" r="4.5" fill="var(--color-gold)"/>
                </svg>
            </span>
            <span class="logo__text">
                <span class="logo__title">Бирюзовое кольцо</span>
                <span class="logo__subtitle">Орловская область</span>
            </span>
        </a>

        <nav class="main-nav" id="main-nav" aria-label="Основная навигация">
            <ul class="main-nav__list">
                <?php foreach ($__navItems as $key => $label): ?>
                    <li>
                        <a href="<?= BASE_URL ?>/<?= e($key) ?>"
                           class="main-nav__link<?= nav_is_active($key, $__currentPath) ?>">
                            <?= e($label) ?>
                        </a>
                    </li>
                <?php endforeach; ?>
            </ul>
        </nav>

        <div class="header-account">
            <?php if ($__user): ?>
                <div class="account-menu">
                    <button type="button" class="account-menu__trigger" id="accountMenuTrigger" aria-haspopup="true" aria-expanded="false">
                        <?php if (!empty($__user['avatar'])): ?>
                            <img src="<?= e(UPLOAD_URL . $__user['avatar']) ?>" alt="" class="account-menu__avatar">
                        <?php else: ?>
                            <span class="account-menu__avatar account-menu__avatar--placeholder">
                                <?= e(userInitials($__user['username'] ?? '')) ?>
                            </span>
                        <?php endif; ?>
                        <span class="account-menu__name"><?= e($__user['username']) ?></span>
                        <svg class="account-menu__chevron" viewBox="0 0 16 16" width="12" height="12" aria-hidden="true" focusable="false">
                            <path d="M3 5.5 8 11l5-5.5" stroke="currentColor" stroke-width="1.8" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
                        </svg>
                    </button>
                    <div class="account-menu__dropdown" id="accountMenuDropdown">
                        <a href="<?= BASE_URL ?>/profile">Личный кабинет</a>
                        <?php if (($__user['role'] ?? '') === 'admin'): ?>
                            <a href="<?= BASE_URL ?>/admin">Админ-панель</a>
                        <?php endif; ?>
                        <a href="<?= BASE_URL ?>/logout">Выйти</a>
                    </div>
                </div>
            <?php else: ?>
                <a href="<?= BASE_URL ?>/login" class="btn btn--ghost">Вход</a>
                <a href="<?= BASE_URL ?>/register" class="btn btn--primary btn--sm">Регистрация</a>
            <?php endif; ?>
        </div>

        <button type="button" class="burger" id="burgerBtn" aria-expanded="false" aria-controls="main-nav" aria-label="Открыть меню">
            <span></span><span></span><span></span>
        </button>
    </div>
</header>
