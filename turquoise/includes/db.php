<?php
/**
 * Подключение к базе данных (PDO Singleton)
 */

class Database
{
    private static ?PDO $instance = null;

    public static function getInstance(): PDO
    {
        if (self::$instance === null) {
            $dsn = sprintf(
                'mysql:host=%s;dbname=%s;charset=%s',
                DB_HOST, DB_NAME, DB_CHARSET
            );
            $options = [
                PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
                PDO::ATTR_EMULATE_PREPARES   => false,
            ];
            try {
                self::$instance = new PDO($dsn, DB_USER, DB_PASS, $options);
            } catch (PDOException $e) {
                if (DEBUG) {
                    die('Ошибка подключения к БД: ' . $e->getMessage());
                }
                die('Ошибка подключения к базе данных.');
            }
        }
        return self::$instance;
    }

    // Запрет клонирования
    private function __clone() {}
}

/**
 * Глобальная функция для получения соединения
 */
function db(): PDO
{
    return Database::getInstance();
}
