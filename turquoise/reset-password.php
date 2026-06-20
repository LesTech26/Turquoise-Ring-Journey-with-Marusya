<?php
// reset-password.php
require_once __DIR__ . '/includes/config.php';
require_once __DIR__ . '/includes/db.php';
require_once __DIR__ . '/includes/functions.php';
require_once __DIR__ . '/includes/auth.php';

$token  = trim($_GET['token'] ?? '');
$errors = [];

if ($_SERVER['REQUEST_METHOD'] === 'POST' && csrf_verify()) {
    $result = Auth::resetPassword(
        trim($_POST['token'] ?? ''),
        $_POST['password'] ?? ''
    );
    if ($result['success']) {
        setFlash('success', 'Пароль успешно изменён. Войдите с новым паролем.');
        redirect('/login');
    } else {
        $errors[] = $result['error'];
    }
}

include __DIR__ . '/templates/header.php';
?>
<main id="main" class="auth-page">
    <h1>Новый пароль</h1>
    <?php foreach ($errors as $err): ?>
        <div class="alert alert--error"><?= e($err) ?></div>
    <?php endforeach; ?>
    <?php if ($token): ?>
        <form method="POST" class="auth-form">
            <?= csrf_field() ?>
            <input type="hidden" name="token" value="<?= e($token) ?>">
            <label>Новый пароль <input type="password" name="password" required></label>
            <button type="submit" class="btn btn--primary">Сохранить пароль</button>
        </form>
    <?php else: ?>
        <p class="alert alert--error">Неверная или устаревшая ссылка.</p>
    <?php endif; ?>
</main>
<?php include __DIR__ . '/templates/footer.php'; ?>
