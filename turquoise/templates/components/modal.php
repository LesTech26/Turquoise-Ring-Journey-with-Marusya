<?php
/**
 * templates/components/modal.php
 * Универсальное модальное окно. Используется для подсказок, итогов игр и т.п.
 *
 * Переменные:
 *   $modalId    string  уникальный id (обязателен)
 *   $modalTitle string  заголовок (опционально)
 *   $modalBody  string  HTML-содержимое тела (опционально; либо передайте
 *                       содержимое через буферизацию перед include)
 *   $modalSize  string  'sm' | 'md' | 'lg' (по умолчанию 'md')
 *
 * Открытие из JS:  document.getElementById('<id>').showModal()  (через main.js helper openModal('id'))
 */
if (empty($modalId)) {
    return;
}
$__size = $modalSize ?? 'md';
?>
<dialog id="<?= e($modalId) ?>" class="modal modal--<?= e($__size) ?>">
    <div class="modal__panel">
        <button type="button" class="modal__close" data-modal-close aria-label="Закрыть">×</button>

        <?php if (!empty($modalTitle)): ?>
            <h2 class="modal__title"><?= e($modalTitle) ?></h2>
        <?php endif; ?>

        <div class="modal__body">
            <?= $modalBody ?? '' ?>
        </div>
    </div>
</dialog>
