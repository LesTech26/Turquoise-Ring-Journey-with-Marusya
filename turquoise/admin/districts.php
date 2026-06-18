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
// CREATE / UPDATE
// ------------------------------------------------------------------
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if (!csrf_verify()) {
        setFlash('error', 'Ошибка безопасности. Повторите попытку.');
        redirect('/admin/districts');
    }

    $data = [
        'name'                 => trim($_POST['name'] ?? ''),
        'slug'                 => slugify(trim($_POST['slug'] ?? $_POST['name'] ?? '')),
        'short_description'    => trim($_POST['short_description'] ?? ''),
        'full_description'     => trim($_POST['full_description'] ?? ''),
        'children_description' => trim($_POST['children_description'] ?? ''),
        'costume_description'  => trim($_POST['costume_description'] ?? ''),
        'map_x'                => (int)($_POST['map_x'] ?? 0),
        'map_y'                => (int)($_POST['map_y'] ?? 0),
        'is_active'            => isset($_POST['is_active']) ? 1 : 0,
    ];

    // Загрузка герба
    if (!empty($_FILES['coat_of_arms']['name'])) {
        $path = uploadFile($_FILES['coat_of_arms'], 'coats');
        if ($path) {
            $data['coat_of_arms'] = $path;
        }
    }

    if ($id > 0) {
        // UPDATE
        $setClauses = implode(', ', array_map(fn($k) => "`$k` = ?", array_keys($data)));
        $stmt = db()->prepare("UPDATE districts SET $setClauses WHERE id = ?");
        $stmt->execute([...array_values($data), $id]);
        setFlash('success', 'Район обновлён.');
    } else {
        // INSERT
        $cols = implode(', ', array_map(fn($k) => "`$k`", array_keys($data)));
        $vals = implode(', ', array_fill(0, count($data), '?'));
        $stmt = db()->prepare("INSERT INTO districts ($cols) VALUES ($vals)");
        $stmt->execute(array_values($data));
        setFlash('success', 'Район добавлен.');
    }

    redirect('/admin/districts');
}

// ------------------------------------------------------------------
// DELETE
// ------------------------------------------------------------------
if ($action === 'delete' && $id > 0) {
    if (!csrf_verify()) {
        setFlash('error', 'Ошибка безопасности.');
        redirect('/admin/districts');
    }
    db()->prepare('DELETE FROM districts WHERE id = ?')->execute([$id]);
    setFlash('success', 'Район удалён.');
    redirect('/admin/districts');
}

// ------------------------------------------------------------------
// Загрузка данных для формы редактирования
// ------------------------------------------------------------------
$district = null;
if (in_array($action, ['edit', 'delete'], true) && $id > 0) {
    $stmt = db()->prepare('SELECT * FROM districts WHERE id = ?');
    $stmt->execute([$id]);
    $district = $stmt->fetch();
}

// ------------------------------------------------------------------
// Список районов
// ------------------------------------------------------------------
$districts = db()->query('SELECT * FROM districts ORDER BY sort_order ASC, name ASC')->fetchAll();

include dirname(__DIR__) . '/templates/admin/header.php';
?>

<h1 class="admin-title">Управление районами</h1>

<?php foreach (getFlash() as $f): ?>
    <div class="alert alert--<?= e($f['type']) ?>"><?= e($f['message']) ?></div>
<?php endforeach; ?>

<?php if ($action === 'list'): ?>

    <a href="?action=add" class="btn btn--primary">+ Добавить район</a>

    <table class="admin-table">
        <thead>
            <tr>
                <th>#</th><th>Название</th><th>Slug</th><th>Активен</th><th>Действия</th>
            </tr>
        </thead>
        <tbody>
        <?php foreach ($districts as $d): ?>
            <tr>
                <td><?= e($d['id']) ?></td>
                <td><?= e($d['name']) ?></td>
                <td><?= e($d['slug']) ?></td>
                <td><?= $d['is_active'] ? 'Да' : 'Нет' ?></td>
                <td>
                    <a href="?action=edit&id=<?= $d['id'] ?>">Редактировать</a>
                    <a href="?action=delete&id=<?= $d['id'] ?>&<?= CSRF_TOKEN_NAME ?>=<?= csrf_token() ?>"
                       onclick="return confirm('Удалить район «<?= e($d['name']) ?>»?')">Удалить</a>
                </td>
            </tr>
        <?php endforeach; ?>
        </tbody>
    </table>

<?php elseif (in_array($action, ['add', 'edit'])): ?>

    <h2><?= $action === 'edit' ? 'Редактировать район' : 'Добавить район' ?></h2>

    <form method="POST" enctype="multipart/form-data" class="admin-form">
        <?= csrf_field() ?>
        <?php if ($id): ?><input type="hidden" name="_id" value="<?= $id ?>"><?php endif; ?>

        <label>Название *
            <input type="text" name="name" required
                   value="<?= e($district['name'] ?? '') ?>">
        </label>
        <label>Slug (URL)
            <input type="text" name="slug"
                   value="<?= e($district['slug'] ?? '') ?>">
        </label>
        <label>Краткое описание
            <textarea name="short_description"><?= e($district['short_description'] ?? '') ?></textarea>
        </label>
        <label>Полное описание
            <textarea name="full_description" rows="8"><?= e($district['full_description'] ?? '') ?></textarea>
        </label>
        <label>Детская версия
            <textarea name="children_description" rows="6"><?= e($district['children_description'] ?? '') ?></textarea>
        </label>
        <label>Описание костюма
            <textarea name="costume_description" rows="4"><?= e($district['costume_description'] ?? '') ?></textarea>
        </label>
        <label>Герб (изображение)
            <input type="file" name="coat_of_arms" accept="image/*">
            <?php if (!empty($district['coat_of_arms'])): ?>
                <img src="<?= e(UPLOAD_URL . $district['coat_of_arms']) ?>" height="60" alt="Герб">
            <?php endif; ?>
        </label>
        <label>Позиция на карте X
            <input type="number" name="map_x" value="<?= e($district['map_x'] ?? 0) ?>">
        </label>
        <label>Позиция на карте Y
            <input type="number" name="map_y" value="<?= e($district['map_y'] ?? 0) ?>">
        </label>
        <label>
            <input type="checkbox" name="is_active" <?= ($district['is_active'] ?? 1) ? 'checked' : '' ?>>
            Активен
        </label>
        <button type="submit" class="btn btn--primary">Сохранить</button>
        <a href="?" class="btn">Отмена</a>
    </form>

<?php endif; ?>

<?php include dirname(__DIR__) . '/templates/admin/footer.php'; ?>
