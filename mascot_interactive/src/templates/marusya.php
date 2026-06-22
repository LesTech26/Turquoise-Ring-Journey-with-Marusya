<?php
/**
 * marusya.php
 * Шаблон маскота Маруся
 * Адаптировано под проект turquoise
 * 
 * Использование в turquoise/templates/header.php:
 * <?php include_once __DIR__ . '/../../mascot_interactive/src/templates/marusya.php'; ?>
 */

// Путь к модулю
$marusya_path = '/mascot_interactive';
?>

<!-- ============================================
     МАСКОТ МАРУСЯ — HTML-шаблон
     ============================================ -->

<div id="marusya-container" aria-label="Маруся - интерактивный гид" role="complementary">
    
    <!-- Сам маскот -->
    <div id="marusya" class="marusya happy" role="button" tabindex="0" aria-label="Маруся - нажми для взаимодействия">
        
        <!-- Аватар -->
        <div class="marusya-avatar-wrapper">
            <img 
                src="<?= $marusya_path ?>/assets/img/marusya/happy.svg" 
                alt="Маруся — счастливая" 
                id="marusya-avatar"
                class="marusya-avatar"
                loading="lazy"
                width="90"
                height="90"
            >
            <!-- Индикатор настроения -->
            <div class="marusya-status" id="marusya-status" aria-hidden="true"></div>
        </div>
        
        <!-- Облачко с речью -->
        <div id="marusya-speech" class="speech-bubble hidden" role="dialog" aria-label="Сообщение от Маруси" aria-live="polite">
            <div class="speech-content">
                <span id="marusya-text" class="speech-text">Привет!</span>
                <div class="speech-actions">
                    <button 
                        id="marusya-speak-btn" 
                        class="speak-btn" 
                        title="Озвучить текст" 
                        aria-label="Озвучить сообщение"
                        type="button"
                    >
                        🔊
                    </button>
                    <button 
                        id="marusya-close-btn" 
                        class="close-speech-btn" 
                        title="Закрыть сообщение" 
                        aria-label="Закрыть сообщение"
                        type="button"
                    >
                        ✕
                    </button>
                </div>
            </div>
            <div class="speech-tail" aria-hidden="true"></div>
        </div>
        
        <!-- Кнопка скрытия -->
        <div class="marusya-actions" aria-hidden="true">
            <button 
                id="marusya-hide" 
                class="marusya-action-btn" 
                title="Спрятать Марусю" 
                aria-label="Спрятать Марусю"
                type="button"
            >
                🙈
            </button>
        </div>
        
    </div>
    
    <!-- Меню -->
    <div id="marusya-menu" class="marusya-menu hidden" role="menu" aria-label="Меню Маруси">
        <ul>
            <li>
                <button data-action="help" role="menuitem" type="button">
                    ❓ Помощь
                </button>
            </li>
            <li>
                <button data-action="reset" role="menuitem" type="button">
                    🔄 Сбросить прогресс
                </button>
            </li>
            <li>
                <button data-action="settings" role="menuitem" type="button">
                    ⚙️ Настройки
                </button>
            </li>
        </ul>
    </div>
    
</div>

<!-- ============================================
     ПОДКЛЮЧЕНИЕ СКРИПТОВ
     ============================================ -->

<!-- 1. Canvas Confetti (для анимаций празднования) -->
<script src="https://cdn.jsdelivr.net/npm/canvas-confetti@1" defer></script>

<!-- 2. Настройка путей -->
<script>
    window.MARUSYA_ASSET_PATH = '<?= $marusya_path ?>/assets/img/marusya/';
</script>

<!-- 3. Модули Маруси -->
<script src="<?= $marusya_path ?>/src/js/dialogues.js" defer></script>
<script src="<?= $marusya_path ?>/src/js/emotions.js" defer></script>
<script src="<?= $marusya_path ?>/src/js/speech.js" defer></script>
<script src="<?= $marusya_path ?>/src/js/tooltips.js" defer></script>
<script src="<?= $marusya_path ?>/src/js/celebrations.js" defer></script>
<script src="<?= $marusya_path ?>/src/js/Marusya.js" defer></script>
<script src="<?= $marusya_path ?>/src/js/main.js" defer></script>