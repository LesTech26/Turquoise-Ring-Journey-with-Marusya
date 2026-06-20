<?php
/**
 * games/coloring.php
 * Макет игры "Раскраска". Логика рисования на Canvas подключается отдельным JS-скриптом.
 */
require_once __DIR__ . '/../includes/config.php';
require_once __DIR__ . '/../includes/db.php';
require_once __DIR__ . '/../includes/functions.php';
require_once __DIR__ . '/../includes/auth.php';

$districtSlug = trim($_GET['district'] ?? '');
$district = null;

if ($districtSlug) {
    $stmt = db()->prepare('SELECT * FROM districts WHERE slug = ? LIMIT 1');
    $stmt->execute([$districtSlug]);
    $district = $stmt->fetch();
}

$gameTitle = 'Раскраска' . ($district ? ': ' . $district['name'] : '');
$gameIcon  = '🎨';
$gameMeta  = null;

$palette = ['#D9534F', '#E8A94A', '#2BAFA0', '#8B5A3C', '#4F9D5D', '#3A2E22', '#FDF6E9', '#1C7A70'];

ob_start();
?>
<div class="coloring-canvas-wrap">
    <!-- Холст для раскраски подключит JS-разработчик игр -->
    <canvas id="coloringCanvas" class="coloring-canvas-placeholder" width="480" height="360"
            role="img" aria-label="Область для раскрашивания костюма">
    </canvas>
</div>

<div class="coloring-palette" id="coloringPalette" role="group" aria-label="Палитра цветов">
    <?php foreach ($palette as $i => $color): ?>
        <button type="button" class="coloring-palette__swatch<?= $i === 0 ? ' is-selected' : '' ?>"
                style="background:<?= e($color) ?>" data-color="<?= e($color) ?>"
                aria-label="Цвет <?= e($color) ?>"></button>
    <?php endforeach; ?>
</div>

<div class="district-page__actions" style="justify-content:center; margin-top:24px;">
    <button type="button" class="btn btn--ghost" id="coloringClear">Очистить</button>
    <button type="button" class="btn btn--primary" id="coloringSave">Сохранить работу</button>
</div>
<?php
$gameBody = ob_get_clean();
require __DIR__ . '/../templates/game-layout.php';
