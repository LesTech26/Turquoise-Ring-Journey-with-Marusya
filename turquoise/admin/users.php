<?php
/**
 * admin/users.php
 * Таблица пользователей: роль, статус, дата регистрации.
 */
require_once __DIR__ . '/../includes/config.php';
require_once __DIR__ . '/../includes/db.php';
require_once __DIR__ . '/../includes/functions.php';
require_once __DIR__ . '/../includes/auth.php';
requireRole('admin');

$adminTitle  = 'Пользователи';
$adminActive = 'users';

if ($_SERVER['REQUEST_METHOD'] === 'POST' && ($_POST['_action'] ?? '') === 'toggle_active') {
    if (!csrf_verify()) {
        setFlash('error', 'Ошибка безопасности. Повторите попытку.');
    } else {
        $id = (int)($_POST['user_id'] ?? 0);
        db()->prepare('UPDATE users SET is_active = NOT is_active WHERE id = ?')->execute([$id]);
        setFlash('success', 'Статус пользователя обновлён.');
    }
    redirect('/admin/users');
}

$users = db()->query(
    'SELECT id, username, email, role, is_active, created_at FROM users ORDER BY created_at DESC'
)->fetchAll();

ob_start();
?>
<div class="admin-table-wrap">
    <table class="admin-table">
        <thead>
            <tr>
                <th>Имя</th>
                <th>Email</th>
                <th>Роль</th>
                <th>Статус</th>
                <th>Регистрация</th>
                <th>Действия</th>
            </tr>
        </thead>
        <tbody>
            <?php foreach ($users as $u): ?>
                <tr>
                    <td><?= e($u['username']) ?></td>
                    <td><?= e($u['email']) ?></td>
                    <td><span class="badge" style="background:var(--color-turquoise-light);color:var(--color-turquoise-dark);"><?= e($u['role']) ?></span></td>
                    <td>
                        <?php if ($u['is_active']): ?>
                            <span class="badge badge--green">Активен</span>
                        <?php else: ?>
                            <span class="badge" style="background:#FBEAE8;color:var(--color-error);">Блокирован</span>
                        <?php endif; ?>
                    </td>
                    <td><?= formatDate($u['created_at'], 'd.m.Y') ?></td>
                    <td class="admin-table__actions">
                        <form method="POST">
                            <?= csrf_field() ?>
                            <input type="hidden" name="_action" value="toggle_active">
                            <input type="hidden" name="user_id" value="<?= (int)$u['id'] ?>">
                            <button type="submit" class="btn btn--sm">
                                <?= $u['is_active'] ? 'Блокировать' : 'Разблокировать' ?>
                            </button>
                        </form>
                    </td>
                </tr>
            <?php endforeach; ?>
        </tbody>
    </table>
</div>
<?php
$adminBody = ob_get_clean();
require __DIR__ . '/_layout.php';
