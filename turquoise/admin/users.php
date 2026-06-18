<?php
define('BASE_PATH', dirname(__DIR__));
require_once dirname(__DIR__) . '/includes/config.php';
require_once dirname(__DIR__) . '/includes/db.php';
require_once dirname(__DIR__) . '/includes/functions.php';
require_once dirname(__DIR__) . '/includes/auth.php';

requireAdmin();

$action = $_GET['action'] ?? 'list';
$id     = isset($_GET['id']) ? (int)$_GET['id'] : 0;

// ------------------------------------------------------------------
// Действия (POST)
// ------------------------------------------------------------------
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if (!csrf_verify()) {
        setFlash('error', 'Ошибка безопасности.');
        redirect('/admin/users');
    }

    $postAction = $_POST['_action'] ?? '';

    // Смена роли
    if ($postAction === 'change_role' && $id > 0) {
        $role = in_array($_POST['role'] ?? '', ['admin','editor','user']) ? $_POST['role'] : 'user';
        db()->prepare('UPDATE users SET role = ? WHERE id = ?')->execute([$role, $id]);
        setFlash('success', 'Роль изменена.');
    }

    // Сброс прогресса
    if ($postAction === 'reset_progress' && $id > 0) {
        db()->prepare('DELETE FROM user_progress WHERE user_id = ?')->execute([$id]);
        db()->prepare('DELETE FROM user_achievements WHERE user_id = ?')->execute([$id]);
        setFlash('success', 'Прогресс пользователя сброшен.');
    }

    redirect('/admin/users');
}

// ------------------------------------------------------------------
// Блокировка / разблокировка (GET)
// ------------------------------------------------------------------
if (in_array($action, ['block', 'unblock']) && $id > 0) {
    if (!csrf_verify()) {
        setFlash('error', 'Ошибка безопасности.');
        redirect('/admin/users');
    }
    // Защита: нельзя заблокировать самого себя
    if ($id !== (int)($_SESSION['user']['id'] ?? 0)) {
        $active = $action === 'unblock' ? 1 : 0;
        db()->prepare('UPDATE users SET is_active = ? WHERE id = ?')->execute([$active, $id]);
        setFlash('success', $action === 'block' ? 'Пользователь заблокирован.' : 'Пользователь разблокирован.');
    }
    redirect('/admin/users');
}

// ------------------------------------------------------------------
// Список пользователей (пагинация)
// ------------------------------------------------------------------
$page  = max(1, (int)($_GET['page'] ?? 1));
$total = (int)db()->query('SELECT COUNT(*) FROM users')->fetchColumn();
$pager = paginate($total, $page);

$stmt = db()->prepare(
    'SELECT id, username, email, role, is_active, created_at
     FROM users
     ORDER BY created_at DESC
     LIMIT ? OFFSET ?'
);
$stmt->bindValue(1, $pager['limit'], PDO::PARAM_INT);
$stmt->bindValue(2, $pager['offset'], PDO::PARAM_INT);
$stmt->execute();
$users = $stmt->fetchAll();

include dirname(__DIR__) . '/templates/admin/header.php';
?>

<h1 class="admin-title">Управление пользователями</h1>

<?php foreach (getFlash() as $f): ?>
    <div class="alert alert--<?= e($f['type']) ?>"><?= e($f['message']) ?></div>
<?php endforeach; ?>

<table class="admin-table">
    <thead>
        <tr>
            <th>#</th><th>Имя</th><th>Email</th><th>Роль</th>
            <th>Статус</th><th>Зарегистрирован</th><th>Действия</th>
        </tr>
    </thead>
    <tbody>
    <?php foreach ($users as $u): ?>
        <tr>
            <td><?= e($u['id']) ?></td>
            <td><?= e($u['username']) ?></td>
            <td><?= e($u['email']) ?></td>
            <td>
                <form method="POST" action="?id=<?= $u['id'] ?>" style="display:inline">
                    <?= csrf_field() ?>
                    <input type="hidden" name="_action" value="change_role">
                    <select name="role" onchange="this.form.submit()">
                        <?php foreach (['admin','editor','user'] as $r): ?>
                            <option <?= $u['role'] === $r ? 'selected' : '' ?>><?= $r ?></option>
                        <?php endforeach; ?>
                    </select>
                </form>
            </td>
            <td><?= $u['is_active'] ? '<span class="badge badge--green">Активен</span>' : '<span class="badge badge--red">Заблокирован</span>' ?></td>
            <td><?= e($u['created_at']) ?></td>
            <td>
                <?php if ($u['id'] !== (int)($_SESSION['user']['id'] ?? 0)): ?>
                    <?php if ($u['is_active']): ?>
                        <a href="?action=block&id=<?= $u['id'] ?>&<?= CSRF_TOKEN_NAME ?>=<?= csrf_token() ?>"
                           onclick="return confirm('Заблокировать пользователя?')">Заблокировать</a>
                    <?php else: ?>
                        <a href="?action=unblock&id=<?= $u['id'] ?>&<?= CSRF_TOKEN_NAME ?>=<?= csrf_token() ?>">Разблокировать</a>
                    <?php endif; ?>
                    |
                    <form method="POST" action="?id=<?= $u['id'] ?>" style="display:inline">
                        <?= csrf_field() ?>
                        <input type="hidden" name="_action" value="reset_progress">
                        <button type="submit" onclick="return confirm('Сбросить прогресс пользователя?')">
                            Сбросить прогресс
                        </button>
                    </form>
                <?php else: ?>
                    <em>Это вы</em>
                <?php endif; ?>
            </td>
        </tr>
    <?php endforeach; ?>
    </tbody>
</table>

<!-- Пагинация -->
<div class="pagination">
    <?php for ($i = 1; $i <= $pager['pages']; $i++): ?>
        <a href="?page=<?= $i ?>" class="<?= $i === $pager['page'] ? 'active' : '' ?>"><?= $i ?></a>
    <?php endfor; ?>
</div>

<?php include dirname(__DIR__) . '/templates/admin/footer.php'; ?>
