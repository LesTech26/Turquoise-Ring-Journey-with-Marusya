<?php
/**
 * Аутентификация: регистрация, вход, восстановление пароля
 */

class Auth
{
    // ------------------------------------------------------------------
    // Регистрация
    // ------------------------------------------------------------------

    /**
     * Регистрация нового пользователя
     *
     * @param string $username
     * @param string $email
     * @param string $password
     * @return array ['success' => bool, 'errors' => [...]]
     */
    public static function register(string $username, string $email, string $password): array
    {
        $errors = self::validateRegistration($username, $email, $password);
        if ($errors) {
            return ['success' => false, 'errors' => $errors];
        }

        // Проверка: email уже занят
        $stmt = db()->prepare('SELECT id FROM users WHERE email = ?');
        $stmt->execute([$email]);
        if ($stmt->fetch()) {
            return ['success' => false, 'errors' => ['Email уже зарегистрирован.']];
        }

        $hash = password_hash($password, PASSWORD_BCRYPT, ['cost' => HASH_COST]);
        $stmt = db()->prepare(
            'INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)'
        );
        $stmt->execute([$username, $email, $hash]);
        $userId = (int)db()->lastInsertId();

        self::loginById($userId);

        return ['success' => true, 'user_id' => $userId];
    }

    /**
     * Валидация данных регистрации
     */
    private static function validateRegistration(string $username, string $email, string $password): array
    {
        $errors = [];
        if (mb_strlen($username) < 2 || mb_strlen($username) > 50) {
            $errors[] = 'Имя пользователя: от 2 до 50 символов.';
        }
        if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
            $errors[] = 'Некорректный email.';
        }
        if (mb_strlen($password) < 8) {
            $errors[] = 'Пароль должен содержать не менее 8 символов.';
        }
        return $errors;
    }

    // ------------------------------------------------------------------
    // Вход
    // ------------------------------------------------------------------

    /**
     * Вход по email и паролю
     *
     * @param string $email
     * @param string $password
     * @param bool   $remember  Запомнить меня
     * @return array ['success' => bool, 'error' => string]
     */
    public static function login(string $email, string $password, bool $remember = false): array
    {
        $stmt = db()->prepare(
            'SELECT id, username, email, password_hash, role, is_active FROM users WHERE email = ?'
        );
        $stmt->execute([$email]);
        $user = $stmt->fetch();

        if (!$user || !password_verify($password, $user['password_hash'])) {
            return ['success' => false, 'error' => 'Неверный email или пароль.'];
        }

        if (!$user['is_active']) {
            return ['success' => false, 'error' => 'Аккаунт заблокирован. Обратитесь к администратору.'];
        }

        self::loginById((int)$user['id']);

        if ($remember) {
            self::setRememberToken((int)$user['id']);
        }

        return ['success' => true];
    }

    /**
     * Загрузить пользователя в сессию по ID
     */
    private static function loginById(int $userId): void
    {
        $stmt = db()->prepare(
            'SELECT id, username, email, role, avatar FROM users WHERE id = ?'
        );
        $stmt->execute([$userId]);
        $user = $stmt->fetch();

        // Регенерация ID сессии для защиты от фиксации
        session_regenerate_id(true);

        $_SESSION['user'] = $user;
    }

    /**
     * Установить cookie «Запомнить меня»
     */
    private static function setRememberToken(int $userId): void
    {
        $token = bin2hex(random_bytes(32));
        $hash  = hash('sha256', $token);

        db()->prepare('UPDATE users SET remember_token = ? WHERE id = ?')
            ->execute([$hash, $userId]);

        setcookie('remember_token', $token, [
            'expires'  => time() + SESSION_LIFETIME,
            'path'     => '/',
            'secure'   => false,
            'httponly' => true,
            'samesite' => 'Lax',
        ]);
    }

    /**
     * Авто-вход по cookie «Запомнить меня»
     */
    public static function loginByRememberToken(): bool
    {
        if (isLoggedIn() || empty($_COOKIE['remember_token'])) {
            return false;
        }

        $hash = hash('sha256', $_COOKIE['remember_token']);
        $stmt = db()->prepare(
            'SELECT id FROM users WHERE remember_token = ? AND is_active = 1'
        );
        $stmt->execute([$hash]);
        $user = $stmt->fetch();

        if (!$user) {
            return false;
        }

        self::loginById((int)$user['id']);
        return true;
    }

    // ------------------------------------------------------------------
    // Выход
    // ------------------------------------------------------------------

    public static function logout(): void
    {
        if (!empty($_SESSION['user']['id'])) {
            db()->prepare('UPDATE users SET remember_token = NULL WHERE id = ?')
                ->execute([$_SESSION['user']['id']]);
        }

        $_SESSION = [];
        session_destroy();

        setcookie('remember_token', '', time() - 3600, '/');
        setcookie(SESSION_NAME, '', time() - 3600, '/');
    }

    // ------------------------------------------------------------------
    // Восстановление пароля
    // ------------------------------------------------------------------

    /**
     * Инициировать восстановление пароля (отправить письмо)
     *
     * @param string $email
     * @return array ['success' => bool, 'error' => string]
     */
    public static function requestPasswordReset(string $email): array
    {
        $stmt = db()->prepare('SELECT id, username FROM users WHERE email = ? AND is_active = 1');
        $stmt->execute([$email]);
        $user = $stmt->fetch();

        // Не сообщаем, существует ли email (защита от перебора)
        if (!$user) {
            return ['success' => true];
        }

        $token    = bin2hex(random_bytes(32));
        $tokenExp = date('Y-m-d H:i:s', time() + 3600); // 1 час

        db()->prepare('UPDATE users SET reset_token = ?, reset_token_exp = ? WHERE id = ?')
            ->execute([$token, $tokenExp, $user['id']]);

        self::sendResetEmail($email, $user['username'], $token);

        return ['success' => true];
    }

    /**
     * Сброс пароля по токену
     *
     * @param string $token
     * @param string $newPassword
     * @return array ['success' => bool, 'error' => string]
     */
    public static function resetPassword(string $token, string $newPassword): array
    {
        if (mb_strlen($newPassword) < 8) {
            return ['success' => false, 'error' => 'Пароль должен содержать не менее 8 символов.'];
        }

        $stmt = db()->prepare(
            'SELECT id FROM users WHERE reset_token = ? AND reset_token_exp > NOW() AND is_active = 1'
        );
        $stmt->execute([$token]);
        $user = $stmt->fetch();

        if (!$user) {
            return ['success' => false, 'error' => 'Ссылка для сброса пароля устарела или недействительна.'];
        }

        $hash = password_hash($newPassword, PASSWORD_BCRYPT, ['cost' => HASH_COST]);
        db()->prepare(
            'UPDATE users SET password_hash = ?, reset_token = NULL, reset_token_exp = NULL WHERE id = ?'
        )->execute([$hash, $user['id']]);

        return ['success' => true];
    }

    /**
     * Отправка письма для сброса пароля
     */
    private static function sendResetEmail(string $to, string $name, string $token): void
    {
        $resetLink = BASE_URL . '/reset-password.php?token=' . urlencode($token);
        $subject   = 'Восстановление пароля — Бирюзовое кольцо';
        $body      = "Здравствуйте, {$name}!\n\n"
            . "Вы запросили восстановление пароля.\n"
            . "Перейдите по ссылке (действительна 1 час):\n\n"
            . $resetLink . "\n\n"
            . "Если вы не запрашивали сброс пароля — проигнорируйте это письмо.\n\n"
            . "С уважением,\nКоманда «Бирюзового кольца»";

        $headers = implode("\r\n", [
            'From: ' . MAIL_FROM_NAME . ' <' . MAIL_FROM . '>',
            'Content-Type: text/plain; charset=UTF-8',
            'X-Mailer: PHP/' . phpversion(),
        ]);

        mail($to, $subject, $body, $headers);
    }
}
