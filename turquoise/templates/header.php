<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Бирюзовое кольцо России</title>
    <link rel="stylesheet" href="<?= BASE_URL ?>/assets/css/style.css">
</head>
<body>
<header class="site-header">
    <a href="<?= BASE_URL ?>/" class="site-logo">Бирюзовое кольцо</a>
    <nav class="site-nav">
        <a href="<?= BASE_URL ?>/games">Игры</a>
        <a href="<?= BASE_URL ?>/achievements">Достижения</a>
        <a href="<?= BASE_URL ?>/media">Медиатека</a>
        <a href="<?= BASE_URL ?>/about">О проекте</a>
        <?php if (isLoggedIn()): ?>
            <a href="<?= BASE_URL ?>/profile"><?= e(currentUser()['username']) ?></a>
            <?php if (hasRole('admin')): ?>
                <a href="<?= BASE_URL ?>/admin">Админ</a>
            <?php endif; ?>
            <a href="<?= BASE_URL ?>/logout">Выйти</a>
        <?php else: ?>
            <a href="<?= BASE_URL ?>/login">Войти</a>
            <a href="<?= BASE_URL ?>/register">Регистрация</a>
        <?php endif; ?>
    </nav>
</header>
