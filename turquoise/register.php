<?php
// register.php
require_once __DIR__ . '/includes/config.php';
require_once __DIR__ . '/includes/db.php';
require_once __DIR__ . '/includes/functions.php';
require_once __DIR__ . '/includes/auth.php';

if (isLoggedIn()) redirect('/profile');

$errors = [];

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if (!csrf_verify()) {
        $errors[] = 'Ошибка безопасности.';
    } elseif (empty($_POST['consent_pd'])) {
        $errors[] = 'Для регистрации необходимо согласиться с политикой обработки персональных данных.';
    } else {
        $result = Auth::register(
            trim($_POST['username'] ?? ''),
            trim($_POST['email'] ?? ''),
            $_POST['password'] ?? ''
        );
        if ($result['success']) {
            redirect('/profile');
        } else {
            $errors = $result['errors'];
        }
    }
}

include __DIR__ . '/templates/header.php';
?>
<main id="main" class="auth-page">
    <h1>Регистрация</h1>
    <?php foreach ($errors as $err): ?>
        <div class="alert alert--error"><?= e($err) ?></div>
    <?php endforeach; ?>
    <form method="POST" class="auth-form">
        <?= csrf_field() ?>
        <label>Имя пользователя <input type="text" name="username" required autocomplete="username"></label>
        <label>Email <input type="email" name="email" required autocomplete="email"></label>
        <label>Пароль <input type="password" name="password" required autocomplete="new-password"></label>
        <label class="consent-checkbox">
            <input type="checkbox" name="consent_pd" value="1" required>
            <span>
                Я согласен(на) с
                <a href="<?= BASE_URL ?>/privacy-policy" class="inline-link" target="_blank" rel="noopener">политикой обработки персональных данных</a>
                и даю согласие на обработку моих персональных данных.
            </span>
        </label>
        <button type="submit" class="btn btn--primary">Зарегистрироваться</button>
        <p><a href="<?= BASE_URL ?>/login">Уже есть аккаунт?</a></p>
    </form>
</main>
<?php include __DIR__ . '/templates/footer.php'; ?>
