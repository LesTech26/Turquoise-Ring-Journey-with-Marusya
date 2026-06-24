<?php
/**
 * games/crossword.php
 * Игра "Кроссворды" - генерация сетки 5×5
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

$gameTitle = 'Кроссворд' . ($district ? ': ' . $district['name'] : '');
$gameIcon  = '';
$gameMeta  = 'Сетка 5×5';

// Пример слов для кроссворда (в реальности можно брать из БД)
$crosswordWords = [
    ['word' => 'ОРЁЛ', 'clue' => 'Главный город области', 'row' => 0, 'col' => 0, 'direction' => 'horizontal'],
    ['word' => 'РЕКА', 'clue' => 'Водоём, протекающий через город', 'row' => 0, 'col' => 0, 'direction' => 'vertical'],
    ['word' => 'ГЕРБ', 'clue' => 'Символ района', 'row' => 2, 'col' => 1, 'direction' => 'horizontal'],
];

ob_start();
?>
<div class="crossword-container">
    <div class="crossword-clues">
        <div class="crossword-clues__section">
            <h3>По горизонтали:</h3>
            <ol class="crossword-clues__list">
                <?php foreach ($crosswordWords as $i => $word): ?>
                    <?php if ($word['direction'] === 'horizontal'): ?>
                        <li><?= e($word['clue']) ?></li>
                    <?php endif; ?>
                <?php endforeach; ?>
            </ol>
        </div>
        <div class="crossword-clues__section">
            <h3>По вертикали:</h3>
            <ol class="crossword-clues__list">
                <?php foreach ($crosswordWords as $i => $word): ?>
                    <?php if ($word['direction'] === 'vertical'): ?>
                        <li><?= e($word['clue']) ?></li>
                    <?php endif; ?>
                <?php endforeach; ?>
            </ol>
        </div>
    </div>

    <div class="crossword-grid" id="crosswordGrid" role="grid" aria-label="Сетка кроссворда 5×5">
        <?php for ($row = 0; $row < 5; $row++): ?>
            <?php for ($col = 0; $col < 5; $col++): ?>
                <input type="text" 
                       class="crossword-cell" 
                       maxlength="1" 
                       data-row="<?= $row ?>" 
                       data-col="<?= $col ?>"
                       aria-label="Ячейка <?= $row + 1 ?>, <?= $col + 1 ?>">
            <?php endfor; ?>
        <?php endfor; ?>
    </div>
</div>

<div class="district-page__actions" style="justify-content:center; margin-top:24px;">
    <button type="button" class="btn btn--ghost" id="crosswordClear">Очистить</button>
    <button type="button" class="btn btn--primary" id="crosswordCheck">Проверить</button>
</div>

<div class="game-result" id="crosswordResult" hidden>
    <p class="game-result__score" id="crosswordScoreValue">🎉</p>
    <p>Кроссворд решён!</p>
    <div class="district-page__actions" style="justify-content:center">
        <?php if ($districtSlug): ?>
            <a href="<?= BASE_URL ?>/district/<?= e($districtSlug) ?>" class="btn btn--ghost">К району</a>
        <?php endif; ?>
        <a href="<?= BASE_URL ?>/games" class="btn btn--primary">Другие игры</a>
    </div>
</div>

<script>
// Данные кроссворда для проверки
const crosswordData = <?= json_encode($crosswordWords) ?>;
</script>
<?php
$gameBody = ob_get_clean();
require __DIR__ . '/../templates/game-layout.php';
?>
