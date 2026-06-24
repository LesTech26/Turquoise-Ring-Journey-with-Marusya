<?php
/**
 * games/true-false.php
 * Игра "Правда/Неправда" - факты об области
 */
require_once __DIR__ . '/../includes/config.php';
require_once __DIR__ . '/../includes/db.php';
require_once __DIR__ . '/../includes/functions.php';
require_once __DIR__ . '/../includes/auth.php';

$gameTitle = 'Правда или Неправда';
$gameIcon  = '';
$gameMeta  = '15 вопросов';

// Примеры утверждений (в реальности можно брать из БД)
$statements = [
    ['text' => 'Орловская область была основана в 1937 году', 'answer' => true, 'explanation' => 'Верно! Орловская область образована 27 сентября 1937 года.'],
    ['text' => 'В области 24 района', 'answer' => true, 'explanation' => 'Правильно! В Орловской области 24 муниципальных района.'],
    ['text' => 'Река Ока не протекает через Орёл', 'answer' => false, 'explanation' => 'Неверно. Река Ока протекает через центр города Орла.'],
    ['text' => 'Тургенев родился в Орловской области', 'answer' => true, 'explanation' => 'Да! И.С. Тургенев родился в Орле в 1818 году.'],
    ['text' => 'Орловская область граничит с Украиной', 'answer' => false, 'explanation' => 'Неправда. Область граничит с Брянской, Калужской, Тульской, Липецкой и Курской областями.'],
];

ob_start();
?>
<div class="true-false-progress">
    Вопрос <span id="tfCurrentQuestion">1</span> из 15
</div>

<div class="true-false-score">
    Правильных: <span id="tfScore">0</span>
</div>

<div class="true-false-statement-container">
    <p class="true-false-statement" id="tfStatement">
        <?= e($statements[0]['text']) ?>
    </p>
</div>

<div class="true-false-buttons" id="tfButtons">
    <button type="button" class="btn btn--large btn--success" id="tfTrue" data-answer="true">
        <span style="font-size:1.5rem; display:block; margin-bottom:8px;">✓</span>
        Правда
    </button>
    <button type="button" class="btn btn--large btn--danger" id="tfFalse" data-answer="false">
        <span style="font-size:1.5rem; display:block; margin-bottom:8px;">✗</span>
        Неправда
    </button>
</div>

<div class="true-false-feedback" id="tfFeedback" hidden>
    <div class="feedback-result" id="tfFeedbackResult"></div>
    <p class="feedback-explanation" id="tfExplanation"></p>
    <button type="button" class="btn btn--primary" id="tfNext">Следующий вопрос →</button>
</div>

<div class="game-result" id="tfResult" hidden>
    <p class="game-result__score" id="tfFinalScore">0</p>
    <p>правильных ответов из 15</p>
    <p style="font-size:1.1rem; margin-top:12px;" id="tfResultMessage"></p>
    <div class="district-page__actions" style="justify-content:center">
        <button type="button" class="btn btn--ghost" id="tfRestart">Играть ещё</button>
        <a href="<?= BASE_URL ?>/games" class="btn btn--primary">Другие игры</a>
    </div>
</div>

<script>
// Данные утверждений для игры
const statementsData = <?= json_encode($statements) ?>;
</script>
<?php
$gameBody = ob_get_clean();
require __DIR__ . '/../templates/game-layout.php';
?>
