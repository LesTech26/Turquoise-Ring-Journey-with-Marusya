<?php
/**
 * games/quiz.php
 * Макет викторины. Логика проверки ответов подключается отдельным JS-скриптом.
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

$questions = [];
if ($district) {
    $stmt = db()->prepare('SELECT * FROM quiz_questions WHERE district_id = ? ORDER BY sort_order ASC');
    $stmt->execute([$district['id']]);
    $questions = $stmt->fetchAll();
}

$gameTitle = 'Викторина' . ($district ? ': ' . $district['name'] : '');
$gameIcon  = '❓';
$gameMeta  = $questions ? 'Вопросов: ' . count($questions) : null;

ob_start();
?>
<div class="quiz-progress" id="quizProgress" aria-hidden="true">
    <?php foreach ($questions as $i => $q): ?>
        <span class="quiz-progress__dot<?= $i === 0 ? ' is-done' : '' ?>"></span>
    <?php endforeach; ?>
    <?php if (!$questions): ?>
        <span class="quiz-progress__dot is-done"></span>
        <span class="quiz-progress__dot"></span>
        <span class="quiz-progress__dot"></span>
    <?php endif; ?>
</div>

<?php if ($questions): ?>
    <?php $q = $questions[0]; ?>
    <p class="quiz-question" id="quizQuestionText" data-question-id="<?= e($q['id']) ?>">
        <?= e($q['question']) ?>
    </p>
    <div class="quiz-options" id="quizOptions" role="group" aria-label="Варианты ответа">
        <button type="button" class="quiz-option" data-option="a">
            <span class="quiz-option__letter">A</span><?= e($q['option_a']) ?>
        </button>
        <button type="button" class="quiz-option" data-option="b">
            <span class="quiz-option__letter">B</span><?= e($q['option_b']) ?>
        </button>
        <button type="button" class="quiz-option" data-option="c">
            <span class="quiz-option__letter">C</span><?= e($q['option_c']) ?>
        </button>
        <button type="button" class="quiz-option" data-option="d">
            <span class="quiz-option__letter">D</span><?= e($q['option_d']) ?>
        </button>
    </div>
<?php else: ?>
    <!-- Заглушка-макет: реальные вопросы появятся после заполнения quiz_questions -->
    <p class="quiz-question" id="quizQuestionText">Какой герб у этого района?</p>
    <div class="quiz-options" id="quizOptions" role="group" aria-label="Варианты ответа">
        <button type="button" class="quiz-option" data-option="a"><span class="quiz-option__letter">A</span>Вариант ответа А</button>
        <button type="button" class="quiz-option" data-option="b"><span class="quiz-option__letter">B</span>Вариант ответа Б</button>
        <button type="button" class="quiz-option" data-option="c"><span class="quiz-option__letter">C</span>Вариант ответа В</button>
        <button type="button" class="quiz-option" data-option="d"><span class="quiz-option__letter">D</span>Вариант ответа Г</button>
    </div>
<?php endif; ?>

<div class="game-result" id="quizResult" hidden>
    <p class="game-result__score" id="quizScoreValue">0</p>
    <p>очков набрано</p>
    <div class="district-page__actions" style="justify-content:center">
        <a href="<?= BASE_URL ?>/district/<?= e($districtSlug) ?>" class="btn btn--ghost">К району</a>
        <a href="<?= BASE_URL ?>/games" class="btn btn--primary">Другие игры</a>
    </div>
</div>
<?php
$gameBody = ob_get_clean();
require __DIR__ . '/../templates/game-layout.php';
