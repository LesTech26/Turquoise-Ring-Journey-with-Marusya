<?php
/**
 * templates/footer.php
 * Общий футер сайта.
 */
if (!function_exists('e')) {
    require_once __DIR__ . '/../includes/functions.php';
}
?>
<footer class="site-footer">
    <div class="site-footer__inner container">

        <div class="site-footer__brand">
            <span class="logo__mark" aria-hidden="true">
                <svg viewBox="0 0 48 48" width="32" height="32" focusable="false">
                    <circle cx="24" cy="24" r="21" fill="var(--color-turquoise)"/>
                    <path d="M24 8c5 6 10 9 10 16a10 10 0 1 1-20 0c0-7 5-10 10-16Z" fill="var(--color-cream)"/>
                    <circle cx="24" cy="26" r="4.5" fill="var(--color-gold)"/>
                </svg>
            </span>
            <p>«Путешествие по бирюзовому кольцу России» — интерактивный образовательный проект о культуре и традициях районов Орловской области: легенды, народные костюмы, викторины и игры для всей семьи.</p>
        </div>

        <nav class="site-footer__links" aria-label="Дополнительные ссылки">
            <h2 class="site-footer__heading">Навигация</h2>
            <a href="<?= BASE_URL ?>/">Карта</a>
            <a href="<?= BASE_URL ?>/games">Игры</a>
            <a href="<?= BASE_URL ?>/achievements">Достижения</a>
            <a href="<?= BASE_URL ?>/media">Медиатека</a>
            <a href="<?= BASE_URL ?>/about">О проекте</a>
            <a href="<?= BASE_URL ?>/contacts">Контакты</a>
            <a href="<?= BASE_URL ?>/privacy-policy">Персональные данные</a>
        </nav>

        <div class="site-footer__social">
            <h2 class="site-footer__heading">Мы в сети</h2>
            <div class="social-icons">
                <a href="#" aria-label="ВКонтакте" class="social-icons__item">VK</a>
                <a href="#" aria-label="Телеграм" class="social-icons__item">TG</a>
                <a href="#" aria-label="Одноклассники" class="social-icons__item">OK</a>
            </div>
        </div>
    </div>

    <div class="site-footer__bottom container">
        <p>&copy; <?= date('Y') ?> Бирюзовое кольцо России. Все права защищены.</p>
    </div>
</footer>

<script src="<?= BASE_URL ?>/assets/js/main.js" defer></script>
<?php if (!empty($extraScripts)): foreach ($extraScripts as $src): ?>
    <script src="<?= e($src) ?>" defer></script>
<?php endforeach; endif; ?>
</body>
</html>
