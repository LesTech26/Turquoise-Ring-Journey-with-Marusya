<?php
// login.php
require_once __DIR__ . '/includes/config.php';
require_once __DIR__ . '/includes/db.php';
require_once __DIR__ . '/includes/functions.php';
require_once __DIR__ . '/includes/auth.php';

if (isLoggedIn()) redirect('/profile');

$error = '';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if (!csrf_verify()) {
        $error = 'Ошибка безопасности.';
    } else {
        $result = Auth::login(
            trim($_POST['email'] ?? ''),
            $_POST['password'] ?? '',
            !empty($_POST['remember'])
        );
        if ($result['success']) {
            $redirect = $_GET['redirect'] ?? '/profile';
            redirect($redirect);
        } else {
            $error = $result['error'];
        }
    }
}

include __DIR__ . '/templates/header.php';
?>
<main class="auth-page">
    <h1>Вход</h1>
    <?php if ($error): ?><div class="alert alert--error"><?= e($error) ?></div><?php endif; ?>
    <form method="POST" class="auth-form">
        <?= csrf_field() ?>
        <label>Email <input type="email" name="email" required autocomplete="email"></label>
        <label>Пароль <input type="password" name="password" required autocomplete="current-password"></label>
        <label><input type="checkbox" name="remember"> Запомнить меня</label>
        <button type="submit" class="btn btn--primary">Войти</button>
        <p><a href="<?= BASE_URL ?>/register">Регистрация</a> · <a href="<?= BASE_URL ?>/forgot-password">Забыли пароль?</a></p>
    </form>
</main>
<?php include __DIR__ . '/templates/footer.php'; ?>
