<?php
// forgot-password.php
require_once __DIR__ . '/includes/config.php';
require_once __DIR__ . '/includes/db.php';
require_once __DIR__ . '/includes/functions.php';
require_once __DIR__ . '/includes/auth.php';

$sent = false;
if ($_SERVER['REQUEST_METHOD'] === 'POST' && csrf_verify()) {
    Auth::requestPasswordReset(trim($_POST['email'] ?? ''));
    $sent = true;
}

include __DIR__ . '/templates/header.php';
?>
<main id="main" class="auth-page">
    <h1>Восстановление пароля</h1>
    <?php if ($sent): ?>
        <div class="alert alert--success">
            Если указанный email зарегистрирован, на него отправлена ссылка для сброса пароля.
        </div>
    <?php else: ?>
        <form method="POST" class="auth-form">
            <?= csrf_field() ?>
            <label>Email <input type="email" name="email" required></label>
            <button type="submit" class="btn btn--primary">Отправить ссылку</button>
        </form>
    <?php endif; ?>
    <p><a href="<?= BASE_URL ?>/login">Вернуться ко входу</a></p>
</main>
<?php include __DIR__ . '/templates/footer.php'; ?>
