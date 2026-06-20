<?php
/**
 * db.php
 * Единая точка получения PDO-подключения.
 */

function db(): PDO
{
    static $pdo = null;

    if ($pdo === null) {
        $dsn = sprintf('mysql:host=%s;dbname=%s;charset=%s', DB_HOST, DB_NAME, DB_CHARSET);
        try {
            $pdo = new PDO($dsn, DB_USER, DB_PASS, [
                PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
                PDO::ATTR_EMULATE_PREPARES   => false,
            ]);
        } catch (PDOException $e) {
            if (defined('APP_DEBUG') && APP_DEBUG) {
                die('Ошибка подключения к БД: ' . $e->getMessage());
            }
            die('Сервис временно недоступен. Попробуйте позже.');
        }
    }

    return $pdo;
}
