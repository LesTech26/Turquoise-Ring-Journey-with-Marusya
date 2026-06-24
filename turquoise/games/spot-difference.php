<?php
/**
 * games/spot-difference.php
 * Игра "Найди отличие" - сравнение костюмов
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

$gameTitle = 'Найди отличие' . ($district ? ': ' . $district['name'] : '');
$gameIcon  = '';
$gameMeta  = '5 отличий';

// Координаты отличий (в процентах от размера изображения)
$differences = [
    ['x' => 25, 'y' => 30, 'size' => 8],
    ['x' => 45, 'y' => 50, 'size' => 10],
    ['x' => 70, 'y' => 25, 'size' => 8],
    ['x' => 60, 'y' => 70, 'size' => 12],
    ['x' => 35, 'y' => 80, 'size' => 9],
];

ob_start();
?>
<div class="spot-info">
    <div class="spot-stat">
        Найдено: <span id="spotFound">0</span> / 5
    </div>
    <div class="spot-stat">
        Время: <span id="spotTime">00:00</span>
    </div>
</div>

<div class="spot-container">
    <div class="spot-image-wrapper">
        <p class="spot-label">Оригинал</p>
        <div class="spot-image-holder" id="spotOriginal">
            <!-- Здесь будет оригинальное изображение костюма -->
            <div class="spot-placeholder">
                <svg width="200" height="200" viewBox="0 0 200 200" style="background:#F5F0E8;border-radius:8px;">
                    <rect width="200" height="200" fill="#E8E8E8"/>
                    <text x="100" y="100" text-anchor="middle" font-size="14" fill="#8B7355">Оригинальное изображение</text>
                    <text x="100" y="120" text-anchor="middle" font-size="12" fill="#8B7355">(загрузить в uploads/)</text>
                </svg>
            </div>
        </div>
    </div>

    <div class="spot-image-wrapper">
        <p class="spot-label">С отличиями</p>
        <div class="spot-image-holder" id="spotModified" role="button" aria-label="Кликните по отличиям">
            <!-- Здесь будет изображение с отличиями -->
            <div class="spot-placeholder">
                <svg width="200" height="200" viewBox="0 0 200 200" style="background:#F5F0E8;border-radius:8px;">
                    <rect width="200" height="200" fill="#E8E8E8"/>
                    <text x="100" y="100" text-anchor="middle" font-size="14" fill="#8B7355">Изображение с отличиями</text>
                    <text x="100" y="120" text-anchor="middle" font-size="12" fill="#8B7355">(загрузить в uploads/)</text>
                </svg>
            </div>
            
            <!-- Маркеры отличий (будут показаны после клика) -->
            <?php foreach ($differences as $i => $diff): ?>
                <div class="spot-marker" 
                     data-diff-id="<?= $i ?>"
                     style="left:<?= $diff['x'] ?>%; top:<?= $diff['y'] ?>%; width:<?= $diff['size'] ?>%; height:<?= $diff['size'] ?>%"
                     hidden></div>
            <?php endforeach; ?>
        </div>
    </div>
</div>

<div class="district-page__actions" style="justify-content:center; margin-top:24px;">
    <button type="button" class="btn btn--ghost" id="spotReset">Начать заново</button>
    <button type="button" class="btn btn--ghost" id="spotHint">Подсказка</button>
</div>

<div class="game-result" id="spotResult" hidden>
    <p class="game-result__score">Отлично!</p>
    <p>Все отличия найдены!</p>
    <p style="font-size:1.1rem; margin-top:8px;">
        Время: <strong id="finalSpotTime">00:00</strong>
    </p>
    <div class="district-page__actions" style="justify-content:center">
        <button type="button" class="btn btn--ghost" id="spotPlayAgain">Играть ещё</button>
        <?php if ($districtSlug): ?>
            <a href="<?= BASE_URL ?>/district/<?= e($districtSlug) ?>" class="btn btn--ghost">К району</a>
        <?php endif; ?>
        <a href="<?= BASE_URL ?>/games" class="btn btn--primary">Другие игры</a>
    </div>
</div>

<script>
// Данные отличий для проверки
const differencesData = <?= json_encode($differences) ?>;
</script>
<?php
$gameBody = ob_get_clean();
require __DIR__ . '/../templates/game-layout.php';
?>
