<?php
/**
 * auth.php
 * Класс Auth — регистрация, вход, восстановление пароля, remember-me.
 */

class Auth
{
    public static function login(string $email, string $password, bool $remember = false): array
    {
        if ($email === '' || $password === '') {
            return ['success' => false, 'error' => 'Заполните email и пароль.'];
        }

        $stmt = db()->prepare('SELECT * FROM users WHERE email = ? LIMIT 1');
        $stmt->execute([$email]);
        $user = $stmt->fetch();

        if (!$user || !password_verify($password, $user['password_hash'])) {
            return ['success' => false, 'error' => 'Неверный email или пароль.'];
        }

        if (!$user['is_active']) {
            return ['success' => false, 'error' => 'Учётная запись отключена.'];
        }

        self::startSession($user);

        if ($remember) {
            $token = bin2hex(random_bytes(32));
            db()->prepare('UPDATE users SET remember_token = ? WHERE id = ?')
                ->execute([$token, $user['id']]);
            setcookie('remember_token', $token, [
                'expires'  => time() + REMEMBER_TOKEN_DAYS * 86400,
                'path'     => BASE_URL . '/',
                'httponly' => true,
                'samesite' => 'Lax',
            ]);
        }

        return ['success' => true];
    }

    public static function register(string $username, string $email, string $password): array
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

        if (!$errors) {
            $stmt = db()->prepare('SELECT id FROM users WHERE email = ?');
            $stmt->execute([$email]);
            if ($stmt->fetch()) {
                $errors[] = 'Этот email уже зарегистрирован.';
            }
        }

        if ($errors) {
            return ['success' => false, 'errors' => $errors];
        }

        $hash = password_hash($password, PASSWORD_BCRYPT, ['cost' => HASH_COST]);
        db()->prepare('INSERT INTO users (username, email, password_hash, role) VALUES (?, ?, ?, "user")')
            ->execute([$username, $email, $hash]);

        $userId = (int)db()->lastInsertId();
        $stmt = db()->prepare('SELECT * FROM users WHERE id = ?');
        $stmt->execute([$userId]);
        self::startSession($stmt->fetch());

        return ['success' => true];
    }

    public static function logout(): void
    {
        if (!empty($_SESSION['user']['id'])) {
            db()->prepare('UPDATE users SET remember_token = NULL WHERE id = ?')
                ->execute([$_SESSION['user']['id']]);
        }
        $_SESSION = [];
        if (ini_get('session.use_cookies')) {
            $params = session_get_cookie_params();
            setcookie(session_name(), '', time() - 42000, $params['path']);
        }
        setcookie('remember_token', '', time() - 42000, BASE_URL . '/');
        session_destroy();
    }

    public static function loginByRememberToken(): void
    {
        if (!empty($_SESSION['user']['id']) || empty($_COOKIE['remember_token'])) {
            return;
        }

        $stmt = db()->prepare('SELECT * FROM users WHERE remember_token = ? LIMIT 1');
        $stmt->execute([$_COOKIE['remember_token']]);
        $user = $stmt->fetch();

        if ($user && $user['is_active']) {
            self::startSession($user);
        }
    }

    public static function requestPasswordReset(string $email): void
    {
        if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
            return;
        }

        $stmt = db()->prepare('SELECT id FROM users WHERE email = ?');
        $stmt->execute([$email]);
        $user = $stmt->fetch();

        if (!$user) {
            return; // не раскрываем существование email
        }

        $token = bin2hex(random_bytes(32));
        $expires = date('Y-m-d H:i:s', time() + RESET_TOKEN_TTL_MIN * 60);

        db()->prepare('UPDATE users SET reset_token = ?, reset_token_exp = ? WHERE id = ?')
            ->execute([$token, $expires, $user['id']]);

        // В реальном проекте здесь отправляется email со ссылкой:
        // BASE_URL . '/reset-password?token=' . $token
    }

    public static function resetPassword(string $token, string $password): array
    {
        if ($token === '' || mb_strlen($password) < 8) {
            return ['success' => false, 'error' => 'Пароль должен содержать не менее 8 символов.'];
        }

        $stmt = db()->prepare('SELECT id, reset_token_exp FROM users WHERE reset_token = ? LIMIT 1');
        $stmt->execute([$token]);
        $user = $stmt->fetch();

        if (!$user || strtotime($user['reset_token_exp']) < time()) {
            return ['success' => false, 'error' => 'Ссылка недействительна или устарела.'];
        }

        $hash = password_hash($password, PASSWORD_BCRYPT, ['cost' => HASH_COST]);
        db()->prepare('UPDATE users SET password_hash = ?, reset_token = NULL, reset_token_exp = NULL WHERE id = ?')
            ->execute([$hash, $user['id']]);

        return ['success' => true];
    }

    private static function startSession(array $user): void
    {
        session_regenerate_id(true);
        $_SESSION['user'] = [
            'id'       => $user['id'],
            'username' => $user['username'],
            'email'    => $user['email'],
            'role'     => $user['role'],
            'avatar'   => $user['avatar'],
        ];
    }
}
