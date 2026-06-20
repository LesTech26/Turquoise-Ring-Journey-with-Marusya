<?php
/**
 * games.php
 * Список доступных игр.
 */
require_once __DIR__ . '/includes/config.php';
require_once __DIR__ . '/includes/db.php';
require_once __DIR__ . '/includes/functions.php';
require_once __DIR__ . '/includes/auth.php';

$pageTitle = 'Игры';

$games = [
    ['slug' => 'quiz',     'icon' => '❓', 'title' => 'Викторина', 'desc' => 'Проверь знание легенд и фактов о районах.'],
    ['slug' => 'puzzle',   'icon' => '🧩', 'title' => 'Пазл',      'desc' => 'Собери герб района из частей.'],
    ['slug' => 'coloring', 'icon' => '🎨', 'title' => 'Раскраска', 'desc' => 'Раскрась элементы народного костюма.'],
];

require_once __DIR__ . '/templates/header.php';
?>
<main id="main" class="page-static" style="max-width:900px;">
    <h1>Игры</h1>
    <p>Выбери игру и район, чтобы начать. Большинство игр доступно сразу со страницы района.</p>

    <div class="district-grid" style="margin-top:32px;">
        <?php foreach ($games as $g): ?>
            <article class="district-card">
                <div class="district-card__medallion">
                    <span style="font-size:2.2rem;"><?= $g['icon'] ?></span>
                </div>
                <div class="district-card__body">
                    <h3 class="district-card__title"><?= e($g['title']) ?></h3>
                    <p class="district-card__desc"><?= e($g['desc']) ?></p>
                </div>
                <a href="<?= BASE_URL ?>/games/<?= e($g['slug']) ?>" class="btn btn--primary btn--sm district-card__cta">
                    Играть
                </a>
            </article>
        <?php endforeach; ?>
    </div>
</main>
<?php require_once __DIR__ . '/templates/footer.php'; ?>
