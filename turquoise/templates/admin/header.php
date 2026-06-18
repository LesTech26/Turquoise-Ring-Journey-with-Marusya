<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Админ-панель — Бирюзовое кольцо</title>
    <link rel="stylesheet" href="<?= BASE_URL ?>/assets/css/admin.css">
</head>
<body class="admin-layout">
<aside class="admin-sidebar">
    <a href="<?= BASE_URL ?>/admin" class="admin-logo">Панель управления</a>
    <nav>
        <a href="<?= BASE_URL ?>/admin/districts">Районы</a>
        <a href="<?= BASE_URL ?>/admin/content">Контент</a>
        <a href="<?= BASE_URL ?>/admin/quiz">Викторина</a>
        <a href="<?= BASE_URL ?>/admin/costume">Костюмы</a>
        <a href="<?= BASE_URL ?>/admin/users">Пользователи</a>
        <a href="<?= BASE_URL ?>/admin/stats">Статистика</a>
        <a href="<?= BASE_URL ?>/logout">Выйти</a>
    </nav>
</aside>
<main class="admin-content">
