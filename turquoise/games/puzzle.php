<?php
/**
 * games/puzzle.php
 * Макет игры "Пазл". Логика drag&drop подключается отдельным JS-скриптом.
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

$gameTitle = 'Пазл' . ($district ? ': ' . $district['name'] : '');
$gameIcon  = '🧩';
$gameMeta  = '4×4';

ob_start();
?>
<p style="text-align:center; color:var(--color-walnut-dark); margin-inline:auto; margin-bottom:1.4em;">
    Собери герб района из 16 деталей. Перетаскивай кусочки на своё место.
</p>

<div class="puzzle-board" id="puzzleBoard" aria-label="Поле пазла">
    <?php for ($i = 0; $i < 16; $i++): ?>
        <div class="puzzle-piece" data-cell="<?= $i ?>"></div>
    <?php endfor; ?>
</div>

<div class="puzzle-tray" id="puzzleTray" aria-label="Детали для сборки">
    <?php for ($i = 0; $i < 6; $i++): ?>
        <div class="puzzle-piece" style="width:54px;height:54px;" data-piece="<?= $i ?>" draggable="true"></div>
    <?php endfor; ?>
</div>

<div class="game-result" id="puzzleResult" hidden>
    <p class="game-result__score">🎉</p>
    <p>Пазл собран!</p>
    <div class="district-page__actions" style="justify-content:center">
        <a href="<?= BASE_URL ?>/district/<?= e($districtSlug) ?>" class="btn btn--ghost">К району</a>
        <a href="<?= BASE_URL ?>/games" class="btn btn--primary">Другие игры</a>
    </div>
</div>
<?php
$gameBody = ob_get_clean();
require __DIR__ . '/../templates/game-layout.php';
