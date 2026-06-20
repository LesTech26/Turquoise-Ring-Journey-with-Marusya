<?php
/**
 * privacy-policy.php
 * Страница политики обработки персональных данных.
 * Полный текст доступен также в виде PDF-документа (см. legal/privacy-policy.pdf).
 */
require_once __DIR__ . '/includes/config.php';
require_once __DIR__ . '/includes/db.php';
require_once __DIR__ . '/includes/functions.php';
require_once __DIR__ . '/includes/auth.php';

$pageTitle = 'Политика обработки персональных данных';

require_once __DIR__ . '/templates/header.php';
?>
<main id="main" class="page-static" style="max-width:820px;">
    <h1>Политика обработки персональных данных</h1>
    <p>
        Настоящий документ определяет порядок обработки и защиты персональных данных
        пользователей сайта «Путешествие по бирюзовому кольцу России» в соответствии
        с Федеральным законом № 152-ФЗ «О персональных данных».
    </p>

    <div class="policy-download">
        <a href="<?= BASE_URL ?>/legal/privacy-policy.pdf" class="btn btn--primary" target="_blank" rel="noopener">
            📄 Открыть полный текст в PDF
        </a>
        <a href="<?= BASE_URL ?>/legal/privacy-policy.pdf" class="btn btn--ghost" download>
            Скачать документ
        </a>
    </div>

    <section class="policy-summary">
        <h2>Кратко о главном</h2>
        <ul>
            <li>Мы собираем минимум данных: имя пользователя, email, пароль (в виде хеша), аватар по желанию, игровой прогресс.</li>
            <li>Данные используются только для работы сайта: вход, личный кабинет, сохранение прогресса.</li>
            <li>Мы не передаём ваши данные третьим лицам, кроме случаев, предусмотренных законом.</li>
            <li>Вы можете в любой момент изменить данные профиля или удалить прогресс в личном кабинете.</li>
            <li>Полные условия — в PDF-документе выше.</li>
        </ul>
    </section>

    <p>
        Если у вас остались вопросы по обработке персональных данных, напишите нам через
        страницу <a href="<?= BASE_URL ?>/contacts" class="inline-link">«Контакты»</a>.
    </p>
</main>
<?php require_once __DIR__ . '/templates/footer.php'; ?>
