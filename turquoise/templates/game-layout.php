<?php
/**
 * templates/game-layout.php
 * Общая обёртка для страниц игр.
 *
 * Переменные, которые должна задать страница игры ДО подключения:
 *   $gameTitle  string  название игры
 *   $gameIcon   string  emoji-иконка
 *   $gameMeta   string  короткая метка справа (например, "Район: Болховский")
 *   $gameBody   string  HTML содержимого игры (буферизуется через ob_start)
 */
require_once __DIR__ . '/header.php';
?>
<main id="main" class="game-layout">
    <div class="game-layout__header">
        <div class="game-layout__title">
            <span class="game-layout__icon" aria-hidden="true"><?= $gameIcon ?? '🎮' ?></span>
            <h1><?= e($gameTitle ?? 'Игра') ?></h1>
        </div>
        <?php if (!empty($gameMeta)): ?>
            <span class="game-layout__meta"><?= e($gameMeta) ?></span>
        <?php endif; ?>
    </div>

    <div class="game-panel">
        <?= $gameBody ?? '' ?>
    </div>
</main>
<?php require_once __DIR__ . '/footer.php'; ?>
