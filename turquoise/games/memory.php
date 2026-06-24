<?php
/**
 * games/memory.php
 * Игра "Мемори" - найди пару
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

$gameTitle = 'Мемори' . ($district ? ': ' . $district['name'] : '');
$gameIcon  = '';
$gameMeta  = '16 карточек';

// Символы для карточек (8 пар) - используем текст вместо эмодзи
$symbols = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'];
$cards = array_merge($symbols, $symbols); // Дублируем для пар
shuffle($cards);

ob_start();
?>
<div class="memory-info">
    <div class="memory-stat">
        Ходов: <span id="memoryMoves">0</span>
    </div>
    <div class="memory-stat">
        Пар найдено: <span id="memoryPairs">0</span> / 8
    </div>
    <div class="memory-stat">
        Время: <span id="memoryTime">00:00</span>
    </div>
</div>

<div class="memory-board" id="memoryBoard" role="grid" aria-label="Игровое поле Мемори">
    <?php foreach ($cards as $index => $symbol): ?>
        <button type="button" 
                class="memory-card" 
                data-card-id="<?= $index ?>"
                data-symbol="<?= e($symbol) ?>"
                aria-label="Карточка <?= $index + 1 ?>">
            <span class="memory-card__back">?</span>
            <span class="memory-card__front"><?= $symbol ?></span>
        </button>
    <?php endforeach; ?>
</div>

<div class="district-page__actions" style="justify-content:center; margin-top:24px;">
    <button type="button" class="btn btn--primary" id="memoryRestart">Начать заново</button>
</div>

<div class="game-result" id="memoryResult" hidden>
    <p class="game-result__score">Победа!</p>
    <p>Все пары найдены!</p>
    <p style="font-size:1.1rem; margin-top:8px;">
        Ходов: <strong id="finalMoves">0</strong> | 
        Время: <strong id="finalTime">00:00</strong>
    </p>
    <div class="district-page__actions" style="justify-content:center">
        <button type="button" class="btn btn--ghost" id="memoryPlayAgain">Играть ещё</button>
        <?php if ($districtSlug): ?>
            <a href="<?= BASE_URL ?>/district/<?= e($districtSlug) ?>" class="btn btn--ghost">К району</a>
        <?php endif; ?>
        <a href="<?= BASE_URL ?>/games" class="btn btn--primary">Другие игры</a>
    </div>
</div>
<?php
$gameBody = ob_get_clean();
require __DIR__ . '/../templates/game-layout.php';
?>
