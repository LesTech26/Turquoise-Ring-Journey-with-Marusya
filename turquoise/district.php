<?php
/**
 * district.php
 * Страница отдельного района: герб, описание, таймлайн, костюмы, переход к играм.
 */
require_once __DIR__ . '/includes/config.php';
require_once __DIR__ . '/includes/db.php';
require_once __DIR__ . '/includes/functions.php';
require_once __DIR__ . '/includes/auth.php';

$slug = trim($_GET['slug'] ?? '');

$stmt = db()->prepare('SELECT * FROM districts WHERE slug = ? AND is_active = 1 LIMIT 1');
$stmt->execute([$slug]);
$district = $stmt->fetch();

if (!$district) {
    http_response_code(404);
    require __DIR__ . '/404.php';
    exit;
}

$pageTitle = $district['name'];

$timeline = db()->prepare('SELECT * FROM timeline_events WHERE district_id = ? ORDER BY sort_order ASC');
$timeline->execute([$district['id']]);
$timeline = $timeline->fetchAll();

$photos = db()->prepare('SELECT * FROM district_photos WHERE district_id = ? ORDER BY sort_order ASC');
$photos->execute([$district['id']]);
$photos = $photos->fetchAll();

$funFacts = [];
if (!empty($district['fun_facts'])) {
    $decoded = json_decode($district['fun_facts'], true);
    if (is_array($decoded)) {
        $funFacts = $decoded;
    }
}

$__user = currentUser();
$isCompleted = false;
if ($__user) {
    $progressStmt = db()->prepare('SELECT is_completed FROM user_progress WHERE user_id = ? AND district_id = ?');
    $progressStmt->execute([$__user['id'], $district['id']]);
    $isCompleted = (bool) $progressStmt->fetchColumn();
}

$coatUrl = !empty($district['coat_of_arms'])
    ? UPLOAD_URL . $district['coat_of_arms']
    : BASE_URL . '/assets/img/coat-placeholder.svg';

require_once __DIR__ . '/templates/header.php';
?>
<main id="main">

    <section class="district-page__hero">
        <div class="container">
            <div class="district-page__coat">
                <img src="<?= e($coatUrl) ?>" alt="Герб района <?= e($district['name']) ?>">
            </div>
            <div>
                <p class="district-page__breadcrumb">
                    <a href="<?= BASE_URL ?>/">Карта районов</a> / <?= e($district['name']) ?>
                </p>
                <h1><?= e($district['name']) ?></h1>
                <?php if ($isCompleted): ?>
                    <span class="badge badge--green">✓ Район пройден</span>
                <?php endif; ?>
            </div>
        </div>
    </section>

    <div class="district-page__body">

        <?php if (!empty($district['full_description'])): ?>
            <section class="district-page__section fade-in">
                <h2>О районе</h2>
                <p><?= nl2br(e($district['full_description'])) ?></p>
            </section>
        <?php endif; ?>

        <?php if (!empty($district['children_description'])): ?>
            <section class="district-page__section fade-in">
                <h2>Детям о районе</h2>
                <p><?= nl2br(e($district['children_description'])) ?></p>
            </section>
        <?php endif; ?>

        <?php if ($timeline): ?>
            <section class="district-page__section fade-in">
                <h2>Из истории</h2>
                <div class="timeline">
                    <?php foreach ($timeline as $event): ?>
                        <div class="timeline__item">
                            <div class="timeline__year"><?= e($event['year']) ?></div>
                            <div>
                                <h3><?= e($event['title']) ?></h3>
                                <?php if (!empty($event['description'])): ?>
                                    <p><?= e($event['description']) ?></p>
                                <?php endif; ?>
                            </div>
                        </div>
                    <?php endforeach; ?>
                </div>
            </section>
        <?php endif; ?>

        <?php if (!empty($district['costume_description'])): ?>
            <section class="district-page__section fade-in">
                <h2>Народный костюм</h2>
                <p><?= nl2br(e($district['costume_description'])) ?></p>
                <!-- Витрина элементов костюма (3D/иллюстрации) подключает другой разработчик -->
                <div id="costume-showcase-placeholder"></div>
            </section>
        <?php endif; ?>

        <?php if ($funFacts): ?>
            <section class="district-page__section fade-in">
                <h2>Любопытные факты</h2>
                <ul>
                    <?php foreach ($funFacts as $fact): ?>
                        <li>🧵 <?= e(is_string($fact) ? $fact : '') ?></li>
                    <?php endforeach; ?>
                </ul>
            </section>
        <?php endif; ?>

        <section class="district-page__section fade-in">
            <h2>Проверь себя</h2>
            <p>Готов узнать, как хорошо ты запомнил легенды этого района?</p>
            <div class="district-page__actions">
                <a href="<?= BASE_URL ?>/games/quiz?district=<?= e($district['slug']) ?>" class="btn btn--primary">Пройти викторину</a>
                <a href="<?= BASE_URL ?>/games/puzzle?district=<?= e($district['slug']) ?>" class="btn btn--ghost">Собрать пазл</a>
            </div>
        </section>

    </div>
</main>
<?php require_once __DIR__ . '/templates/footer.php'; ?>
