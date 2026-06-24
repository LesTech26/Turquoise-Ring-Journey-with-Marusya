<?php
/**
 * games/guess-district.php
 * Игра "Угадай район" - выбор из 3 вариантов
 */
require_once __DIR__ . '/../includes/config.php';
require_once __DIR__ . '/../includes/db.php';
require_once __DIR__ . '/../includes/functions.php';
require_once __DIR__ . '/../includes/auth.php';

$gameTitle = 'Угадай район';
$gameIcon  = '';
$gameMeta  = '10 раундов';

// Загружаем районы для игры
$stmt = db()->prepare('SELECT id, name, slug, coat_image FROM districts ORDER BY RAND() LIMIT 10');
$stmt->execute();
$districts = $stmt->fetchAll();

ob_start();
?>
<div class="guess-game-container">
    <div class="guess-progress" id="guessProgress">
        Раунд <span id="currentRound">1</span> из 10
    </div>

    <div class="guess-score" id="guessScore">
        Очки: <span id="scoreValue">0</span>
    </div>

    <div class="guess-image-container" id="guessImageContainer">
        <?php if (!empty($districts[0]['coat_image'])): ?>
            <img src="<?= BASE_URL ?>/uploads/coats/<?= e($districts[0]['coat_image']) ?>" 
                 alt="Герб района" 
                 class="guess-coat-image"
                 id="guessCoatImage">
        <?php else: ?>
            <img src="<?= BASE_URL ?>/assets/img/coat-placeholder.svg" 
                 alt="Герб района" 
                 class="guess-coat-image"
                 id="guessCoatImage">
        <?php endif; ?>
    </div>

    <p class="guess-question">Какой это район?</p>

    <div class="guess-options" id="guessOptions" role="group" aria-label="Варианты ответов">
        <button type="button" class="guess-option" data-district-id="1">
            Вариант 1
        </button>
        <button type="button" class="guess-option" data-district-id="2">
            Вариант 2
        </button>
        <button type="button" class="guess-option" data-district-id="3">
            Вариант 3
        </button>
    </div>

    <div class="guess-feedback" id="guessFeedback" hidden>
        <p class="guess-feedback__text" id="feedbackText"></p>
    </div>
</div>

<div class="game-result" id="guessResult" hidden>
    <p class="game-result__score" id="finalScore">0</p>
    <p>очков из 100</p>
    <div class="district-page__actions" style="justify-content:center">
        <button type="button" class="btn btn--ghost" id="guessRestart">Играть ещё</button>
        <a href="<?= BASE_URL ?>/games" class="btn btn--primary">Другие игры</a>
    </div>
</div>

<script>
// Данные районов для игры
const districtsData = <?= json_encode($districts) ?>;
</script>
<?php
$gameBody = ob_get_clean();
require __DIR__ . '/../templates/game-layout.php';
?>
