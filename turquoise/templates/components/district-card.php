<?php
/**
 * templates/components/district-card.php
 * Карточка района. Ожидает переменную $district (массив из таблицы districts,
 * опционально с полем is_completed из user_progress при наличии прогресса).
 *
 * Пример использования:
 *   foreach ($districts as $district) {
 *       include __DIR__ . '/components/district-card.php';
 *   }
 */
if (!isset($district) || !is_array($district)) {
    return;
}

$__completed = !empty($district['is_completed']);
$__coat = !empty($district['coat_of_arms'])
    ? UPLOAD_URL . $district['coat_of_arms']
    : BASE_URL . '/assets/img/coat-placeholder.svg';
?>
<article class="district-card<?= $__completed ? ' district-card--completed' : '' ?>">
    <div class="district-card__medallion">
        <img src="<?= e($__coat) ?>" alt="Герб: <?= e($district['name']) ?>" loading="lazy">
        <?php if ($__completed): ?>
            <span class="district-card__badge" title="Район пройден">✓</span>
        <?php endif; ?>
    </div>

    <div class="district-card__body">
        <h3 class="district-card__title"><?= e($district['name']) ?></h3>
        <?php if (!empty($district['short_description'])): ?>
            <p class="district-card__desc"><?= e(mb_strimwidth($district['short_description'], 0, 120, '…')) ?></p>
        <?php endif; ?>
    </div>

    <a href="<?= BASE_URL ?>/district/<?= e($district['slug']) ?>" class="btn btn--primary btn--sm district-card__cta">
        Подробнее
    </a>
</article>
