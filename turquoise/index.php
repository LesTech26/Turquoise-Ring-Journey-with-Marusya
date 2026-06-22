<?php
/**
 * index.php
 * Главная страница: герой-блок, Маруся (маскот), список районов.
 */
require_once __DIR__ . '/includes/config.php';
require_once __DIR__ . '/includes/db.php';
require_once __DIR__ . '/includes/functions.php';
require_once __DIR__ . '/includes/auth.php';

$pageTitle = 'Карта районов';

$__user = currentUser();
$__userId = $__user['id'] ?? null;

if ($__userId) {
    $stmt = db()->prepare(
        'SELECT d.*, up.is_completed
         FROM districts d
         LEFT JOIN user_progress up ON up.district_id = d.id AND up.user_id = ?
         WHERE d.is_active = 1
         ORDER BY d.sort_order ASC'
    );
    $stmt->execute([$__userId]);
} else {
    $stmt = db()->query(
        'SELECT * FROM districts WHERE is_active = 1 ORDER BY sort_order ASC'
    );
}
$districts = $stmt->fetchAll();

$totalDistricts = count($districts);
$completedDistricts = $__userId ? array_sum(array_column($districts, 'is_completed')) : 0;

require_once __DIR__ . '/templates/header.php';
?>
<main id="main" class="page-home">

    <section class="hero">
        <div class="hero__inner container">
            <div class="hero__text">
                <p class="hero__eyebrow">Орловская область</p>
                <h1 class="hero__title">Путешествие по&nbsp;бирюзовому кольцу России</h1>
                <p class="hero__lead">
                    Открой 24 района, собери народные костюмы, отгадай легенды
                    и заработай достижения вместе с Марусей — твоим проводником
                    по орловской старине.
                </p>
                <div class="hero__actions">
                    <a href="#districts" class="btn btn--primary">Начать путешествие</a>
                    <?php if (!$__userId): ?>
                        <a href="<?= BASE_URL ?>/register" class="btn btn--ghost">Создать профиль</a>
                    <?php endif; ?>
                </div>
                <?php if ($__userId): ?>
                    <p class="hero__progress">
                        Пройдено районов: <strong><?= $completedDistricts ?> / <?= $totalDistricts ?></strong>
                    </p>
                <?php endif; ?>
            </div>

            <div class="hero__mascot" aria-hidden="true">
                <!-- Место для маскота «Маруся» — анимацию подключает другой разработчик -->
                <div id="marusya-placeholder" class="mascot-placeholder">
                    <svg viewBox="0 0 220 260" width="220" height="260" focusable="false">
                        <ellipse cx="110" cy="235" rx="70" ry="14" fill="var(--color-shadow)" opacity="0.15"/>
                        <path d="M110 40c34 0 58 26 58 64 0 46-26 96-58 130-32-34-58-84-58-130 0-38 24-64 58-64Z" fill="var(--color-red)"/>
                        <path d="M110 60c24 0 40 18 40 46 0 32-18 66-40 92-22-26-40-60-40-92 0-28 16-46 40-46Z" fill="var(--color-cream)"/>
                        <circle cx="110" cy="96" r="26" fill="#FAD9B5"/>
                        <path d="M84 88c4-18 44-18 52 0" stroke="var(--color-walnut)" stroke-width="4" fill="none" stroke-linecap="round"/>
                        <circle cx="99" cy="96" r="3.5" fill="var(--color-walnut)"/>
                        <circle cx="121" cy="96" r="3.5" fill="var(--color-walnut)"/>
                        <path d="M100 108c4 4 16 4 20 0" stroke="var(--color-walnut)" stroke-width="3" fill="none" stroke-linecap="round"/>
                    </svg>
                    <p class="mascot-placeholder__label">Маруся скоро оживёт ✨</p>
                </div>
            </div>
        </div>
    </section>

    <section id="districts" class="districts-section container">
        <header class="districts-section__header">
            <h2>Карта районов</h2>
            <p>Выбери район Орловской области, чтобы узнать его легенды, костюмы и сыграть в викторину.</p>
        </header>

        <?php if ($districts): ?>
            <div class="district-grid">
                <?php foreach ($districts as $district): ?>
                    <?php include __DIR__ . '/templates/components/district-card.php'; ?>
                <?php endforeach; ?>
            </div>
        <?php else: ?>
            <p class="empty-state">Районы пока не добавлены. Загляните позже!</p>
        <?php endif; ?>
    </section>

</main>
<?php
require_once __DIR__ . '/templates/footer.php';
