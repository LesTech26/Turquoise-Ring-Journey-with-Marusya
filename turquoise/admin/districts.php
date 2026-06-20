<?php
/**
 * admin/districts.php
 * Таблица районов с действиями "Редактировать" / "Удалить".
 * Удаление защищено CSRF-токеном.
 */
require_once __DIR__ . '/../includes/config.php';
require_once __DIR__ . '/../includes/db.php';
require_once __DIR__ . '/../includes/functions.php';
require_once __DIR__ . '/../includes/auth.php';
requireRole('admin');

$adminTitle  = 'Районы';
$adminActive = 'districts';

if ($_SERVER['REQUEST_METHOD'] === 'POST' && ($_POST['_action'] ?? '') === 'delete_district') {
    if (!csrf_verify()) {
        setFlash('error', 'Ошибка безопасности. Повторите попытку.');
    } else {
        $id = (int)($_POST['district_id'] ?? 0);
        db()->prepare('DELETE FROM districts WHERE id = ?')->execute([$id]);
        setFlash('success', 'Район удалён.');
    }
    redirect('/admin/districts');
}

$districts = db()->query('SELECT * FROM districts ORDER BY sort_order ASC')->fetchAll();

ob_start();
?>
<div style="margin-bottom:20px; display:flex; justify-content:flex-end;">
    <a href="<?= BASE_URL ?>/admin/content" class="btn btn--primary">+ Добавить район</a>
</div>

<div class="admin-table-wrap">
    <table class="admin-table">
        <thead>
            <tr>
                <th>#</th>
                <th>Название</th>
                <th>Slug</th>
                <th>Статус</th>
                <th>Действия</th>
            </tr>
        </thead>
        <tbody>
            <?php foreach ($districts as $d): ?>
                <tr>
                    <td><?= (int)$d['sort_order'] ?></td>
                    <td><?= e($d['name']) ?></td>
                    <td><code><?= e($d['slug']) ?></code></td>
                    <td>
                        <?php if ($d['is_active']): ?>
                            <span class="badge badge--green">Активен</span>
                        <?php else: ?>
                            <span class="badge" style="background:#F3E6CC;color:#8B5A3C;">Скрыт</span>
                        <?php endif; ?>
                    </td>
                    <td class="admin-table__actions">
                        <a href="<?= BASE_URL ?>/admin/content?district=<?= e($d['slug']) ?>" class="btn btn--sm">Редактировать</a>
                        <form method="POST" onsubmit="return confirm('Удалить район «<?= e($d['name']) ?>»? Это действие необратимо.')">
                            <?= csrf_field() ?>
                            <input type="hidden" name="_action" value="delete_district">
                            <input type="hidden" name="district_id" value="<?= (int)$d['id'] ?>">
                            <button type="submit" class="btn btn--sm btn--danger">Удалить</button>
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
