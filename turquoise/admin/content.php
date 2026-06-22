<?php
/**
 * admin/content.php
 * Форма редактирования контента района (описания, легенды, костюм).
 * Поле "full_description" — WYSIWYG (подключается отдельным JS-редактором,
 * например TipTap/Quill; здесь — разметка-заглушка с data-wysiwyg).
 */
require_once __DIR__ . '/../includes/config.php';
require_once __DIR__ . '/../includes/db.php';
require_once __DIR__ . '/../includes/functions.php';
require_once __DIR__ . '/../includes/auth.php';
requireRole('admin', 'editor');

$adminTitle  = 'Контент района';
$adminActive = 'content';

$slug = trim($_GET['district'] ?? '');
$district = null;

if ($slug) {
    $stmt = db()->prepare('SELECT * FROM districts WHERE slug = ? LIMIT 1');
    $stmt->execute([$slug]);
    $district = $stmt->fetch();
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if (!csrf_verify()) {
        setFlash('error', 'Ошибка безопасности. Повторите попытку.');
    } else {
        $name      = trim($_POST['name'] ?? '');
        $newSlug   = trim($_POST['slug'] ?? '');
        $shortDesc = trim($_POST['short_description'] ?? '');
        $fullDesc  = trim($_POST['full_description'] ?? '');
        $childDesc = trim($_POST['children_description'] ?? '');
        $costDesc  = trim($_POST['costume_description'] ?? '');

        if ($district) {
            db()->prepare(
                'UPDATE districts SET name=?, slug=?, short_description=?, full_description=?,
                 children_description=?, costume_description=? WHERE id=?'
            )->execute([$name, $newSlug, $shortDesc, $fullDesc, $childDesc, $costDesc, $district['id']]);
            setFlash('success', 'Район обновлён.');
        } else {
            db()->prepare(
                'INSERT INTO districts (name, slug, short_description, full_description, children_description, costume_description)
                 VALUES (?, ?, ?, ?, ?, ?)'
            )->execute([$name, $newSlug, $shortDesc, $fullDesc, $childDesc, $costDesc]);
            setFlash('success', 'Район создан.');
        }
        redirect('/admin/districts');
    }
}

ob_start();
?>
<form method="POST" class="admin-form">
    <?= csrf_field() ?>

    <label>Название района
        <input type="text" name="name" value="<?= e($district['name'] ?? '') ?>" required>
    </label>

    <label>Slug (для URL)
        <input type="text" name="slug" value="<?= e($district['slug'] ?? '') ?>" required pattern="[a-z0-9-]+">
    </label>

    <label>Краткое описание (для карточки)
        <textarea name="short_description" rows="2"><?= e($district['short_description'] ?? '') ?></textarea>
    </label>

    <label>Полное описание / легенда (WYSIWYG)
        <textarea name="full_description" rows="8" data-wysiwyg><?= e($district['full_description'] ?? '') ?></textarea>
    </label>

    <label>Описание для детей
        <textarea name="children_description" rows="5" data-wysiwyg><?= e($district['children_description'] ?? '') ?></textarea>
    </label>

    <label>Описание народного костюма
        <textarea name="costume_description" rows="5" data-wysiwyg><?= e($district['costume_description'] ?? '') ?></textarea>
    </label>

    <div style="display:flex; gap:12px;">
        <button type="submit" class="btn btn--primary">Сохранить</button>
        <a href="<?= BASE_URL ?>/admin/districts" class="btn btn--ghost">Отмена</a>
    </div>
</form>
<?php
$adminBody = ob_get_clean();
require __DIR__ . '/_layout.php';
