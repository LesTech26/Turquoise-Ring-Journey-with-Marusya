<?php
/**
 * games/dress-up.php
 * Игра "Собери костюм" - перетаскивание элементов
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

$gameTitle = 'Собери костюм' . ($district ? ': ' . $district['name'] : '');
$gameIcon  = '';
$gameMeta  = 'Перетащи элементы';

// Элементы костюма
$costumeElements = [
    ['id' => 1, 'name' => 'Рубаха', 'type' => 'top', 'color' => '#FDF6E9'],
    ['id' => 2, 'name' => 'Сарафан', 'type' => 'dress', 'color' => '#D9534F'],
    ['id' => 3, 'name' => 'Передник', 'type' => 'apron', 'color' => '#E8A94A'],
    ['id' => 4, 'name' => 'Кокошник', 'type' => 'headwear', 'color' => '#2BAFA0'],
    ['id' => 5, 'name' => 'Платок', 'type' => 'headwear', 'color' => '#D9534F'],
];

ob_start();
?>
<div class="dress-up-container">
    <div class="dress-up-mannequin" id="dressMannequin" aria-label="Манекен для примерки">
        <div class="mannequin-slot" data-slot="headwear" aria-label="Головной убор">
            <span class="mannequin-slot__label">Голова</span>
        </div>
        <div class="mannequin-slot" data-slot="top" aria-label="Верх">
            <span class="mannequin-slot__label">Верх</span>
        </div>
        <div class="mannequin-slot" data-slot="dress" aria-label="Платье">
            <span class="mannequin-slot__label">Платье</span>
        </div>
        <div class="mannequin-slot" data-slot="apron" aria-label="Передник">
            <span class="mannequin-slot__label">Передник</span>
        </div>
    </div>

    <div class="dress-up-wardrobe" id="dressWardrobe" aria-label="Гардероб с элементами костюма">
        <?php foreach ($costumeElements as $element): ?>
            <div class="costume-element" 
                 draggable="true"
                 data-element-id="<?= $element['id'] ?>"
                 data-element-type="<?= e($element['type']) ?>"
                 style="background-color: <?= e($element['color']) ?>">
                <span class="costume-element__name"><?= e($element['name']) ?></span>
            </div>
        <?php endforeach; ?>
    </div>
</div>

<div class="district-page__actions" style="justify-content:center; margin-top:24px;">
    <button type="button" class="btn btn--ghost" id="dressReset">Сбросить</button>
    <button type="button" class="btn btn--primary" id="dressSave">Сохранить образ</button>
</div>

<div class="game-result" id="dressResult" hidden>
    <p class="game-result__score">Готово!</p>
    <p>Костюм собран!</p>
    <div class="district-page__actions" style="justify-content:center">
        <?php if ($districtSlug): ?>
            <a href="<?= BASE_URL ?>/district/<?= e($districtSlug) ?>" class="btn btn--ghost">К району</a>
        <?php endif; ?>
        <a href="<?= BASE_URL ?>/games" class="btn btn--primary">Другие игры</a>
    </div>
</div>

<script>
// Данные элементов костюма
const costumeData = <?= json_encode($costumeElements) ?>;
</script>
<?php
$gameBody = ob_get_clean();
require __DIR__ . '/../templates/game-layout.php';
?>
